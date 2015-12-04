/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.loanschedule.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.loanaccount.data.LoanTermVariationsData;
import org.mifosplatform.portfolio.loanaccount.data.ScheduleGeneratorDTO;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.mifosplatform.portfolio.loanaccount.domain.LoanAccountDomainService;
import org.mifosplatform.portfolio.loanaccount.domain.LoanTermVariations;
import org.mifosplatform.portfolio.loanaccount.service.LoanAssembler;
import org.mifosplatform.portfolio.loanaccount.service.LoanUtilService;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanScheduleWritePlatformServiceImpl implements LoanScheduleWritePlatformService {

    private final LoanAccountDomainService loanAccountDomainService;
    private final LoanAssembler loanAssembler;
    private final LoanScheduleAssembler loanScheduleAssembler;
    private final PlatformSecurityContext context;
    private final LoanUtilService loanUtilService;

    @Autowired
    public LoanScheduleWritePlatformServiceImpl(final LoanAccountDomainService loanAccountDomainService,
            final LoanScheduleAssembler loanScheduleAssembler, final LoanAssembler loanAssembler, final PlatformSecurityContext context,
            final LoanUtilService loanUtilService) {
        this.loanAccountDomainService = loanAccountDomainService;
        this.loanScheduleAssembler = loanScheduleAssembler;
        this.loanAssembler = loanAssembler;
        this.context = context;
        this.loanUtilService = loanUtilService;
    }

    @Override
    public CommandProcessingResult addLoanScheduleVariations(final Long loanId, final JsonCommand command) {
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        this.loanScheduleAssembler.assempleVariableScheduleFrom(loan, command.json());
        List<LoanTermVariations> loanTermVariations = loan.getLoanTermVariations();
        List<LoanTermVariations> newVariations = new ArrayList<>();
        for (LoanTermVariations termVariations : loanTermVariations) {
            if (termVariations.getId() == null) {
                newVariations.add(termVariations);
            }
        }
        this.loanAccountDomainService.saveLoanWithDataIntegrityViolationChecks(loan);
        final Map<String, Object> changes = new HashMap<>();
        List<LoanTermVariationsData> data = new ArrayList<>();
        for (LoanTermVariations termVariations : newVariations) {
            data.add(termVariations.toData());
        }
        changes.put("loanTermVariations", data);
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withLoanId(loanId) //
                .with(changes) //
                .build();
    }

    @Override
    public CommandProcessingResult deleteLoanScheduleVariations(final Long loanId) {
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        List<LoanTermVariations> variations = loan.getLoanTermVariations();
        List<Long> deletedVariations = new ArrayList<>(variations.size());
        for (LoanTermVariations loanTermVariations : variations) {
            deletedVariations.add(loanTermVariations.getId());
        }
        final Map<String, Object> changes = new HashMap<>();
        changes.put("removedEntityIds", deletedVariations);
        loan.getLoanTermVariations().clear();
        ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan);
        AppUser currentUser = this.context.getAuthenticatedUserIfPresent();
        loan.regenerateRepaymentSchedule(scheduleGeneratorDTO, currentUser);
        this.loanAccountDomainService.saveLoanWithDataIntegrityViolationChecks(loan);
        return new CommandProcessingResultBuilder() //
                .withLoanId(loanId) //
                .with(changes) //
                .build();
    }

}
