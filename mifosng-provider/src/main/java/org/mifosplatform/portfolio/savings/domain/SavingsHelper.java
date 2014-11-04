/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.LocalDateInterval;
import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;
import org.mifosplatform.organisation.monetary.domain.Money;
import org.mifosplatform.portfolio.account.service.AccountTransfersReadPlatformService;
import org.mifosplatform.portfolio.calendar.domain.CalendarEntityType;
import org.mifosplatform.portfolio.calendar.domain.CalendarInstance;
import org.mifosplatform.portfolio.calendar.domain.CalendarInstanceRepository;
import org.mifosplatform.portfolio.calendar.domain.CalendarType;
import org.mifosplatform.portfolio.calendar.service.CalendarUtils;
import org.mifosplatform.portfolio.savings.domain.interest.CompoundInterestHelper;
import org.mifosplatform.portfolio.savings.domain.interest.PostingPeriod;

public final class SavingsHelper {

    private final AccountTransfersReadPlatformService accountTransfersReadPlatformService;
    private final CalendarInstanceRepository calendarInstanceRepository;

    public SavingsHelper(AccountTransfersReadPlatformService accountTransfersReadPlatformService,
            CalendarInstanceRepository calendarInstanceRepository) {
        this.accountTransfersReadPlatformService = accountTransfersReadPlatformService;
        this.calendarInstanceRepository = calendarInstanceRepository;
    }

    private final CompoundInterestHelper compoundInterestHelper = new CompoundInterestHelper();

    public List<LocalDateInterval> determineInterestPostingPeriods(final LocalDate startInterestCalculationLocalDate,
            final LocalDate interestPostingUpToDate, final Long savingsId) {

        final List<LocalDateInterval> postingPeriods = new ArrayList<>();

        LocalDate periodStartDate = startInterestCalculationLocalDate;
        LocalDate periodEndDate = periodStartDate;

        while (!periodStartDate.isAfter(interestPostingUpToDate)) {

            final LocalDate interestPostingLocalDate = determineInterestPostingPeriodEndDateFrom(periodStartDate, savingsId);

            periodEndDate = interestPostingLocalDate.minusDays(1);

            postingPeriods.add(LocalDateInterval.create(periodStartDate, periodEndDate));

            periodStartDate = interestPostingLocalDate;
        }

        return postingPeriods;
    }

    private LocalDate determineInterestPostingPeriodEndDateFrom(final LocalDate periodStartDate,
            final Long savingsId) {

        CalendarInstance calendarInstance = getPostingCalendar(savingsId);
        return CalendarUtils.getNextScheduleDate(calendarInstance.getCalendar(), periodStartDate);
    }

    public Money calculateInterestForAllPostingPeriods(final MonetaryCurrency currency, final List<PostingPeriod> allPeriods,
            LocalDate accountLockedUntil, Boolean immediateWithdrawalOfInterest) {
        return this.compoundInterestHelper.calculateInterestForAllPostingPeriods(currency, allPeriods, accountLockedUntil,
                immediateWithdrawalOfInterest);
    }

    public Collection<Long> fetchPostInterestTransactionIds(Long accountId) {
        return this.accountTransfersReadPlatformService.fetchPostInterestTransactionIds(accountId);
    }

    public CalendarInstance getGroupMeeting(final long groupId, final Integer entityType) {
        return this.calendarInstanceRepository.findByEntityIdAndEntityTypeIdAndCalendarTypeId(groupId, entityType,
                CalendarType.COLLECTION.getValue());
    }

    public CalendarInstance getPostingCalendar(final long entityId) {
        return this.calendarInstanceRepository.findByEntityIdAndEntityTypeIdAndCalendarTypeId(entityId,
                CalendarEntityType.SAVINGS.getValue(), CalendarType.INTEREST_POSTING.getValue());
    }
}