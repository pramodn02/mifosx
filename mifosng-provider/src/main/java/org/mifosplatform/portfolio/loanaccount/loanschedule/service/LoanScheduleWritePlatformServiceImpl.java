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
import org.mifosplatform.portfolio.loanaccount.data.LoanTermVariationsData;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.mifosplatform.portfolio.loanaccount.domain.LoanAccountDomainService;
import org.mifosplatform.portfolio.loanaccount.domain.LoanTermVariations;
import org.mifosplatform.portfolio.loanaccount.service.LoanAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanScheduleWritePlatformServiceImpl implements LoanScheduleWritePlatformService {

    private final LoanAccountDomainService loanAccountDomainService;
    private final LoanAssembler loanAssembler;
    private final LoanScheduleAssembler loanScheduleAssembler;

    @Autowired
    public LoanScheduleWritePlatformServiceImpl(final LoanAccountDomainService loanAccountDomainService,
            final LoanScheduleAssembler loanScheduleAssembler, final LoanAssembler loanAssembler) {
        this.loanAccountDomainService = loanAccountDomainService;
        this.loanScheduleAssembler = loanScheduleAssembler;
        this.loanAssembler = loanAssembler;
    }

    @Override
    public CommandProcessingResult modifyLoanSchedule(final Long loanId, final JsonCommand command) {
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
                .withEntityId(loan.getId()) //
                .withLoanId(loanId) //
                .with(changes) //
                .build();
    }

}
