ALTER TABLE `m_savings_product`
	ADD COLUMN `sync_interest_posting_with_meeting` TINYINT(1) NOT NULL DEFAULT '0' AFTER `min_balance_for_interest_calculation`;

ALTER TABLE `m_deposit_product_term_and_preclosure`
	ADD COLUMN `post_interest_as_per_financial_year` TINYINT NOT NULL DEFAULT '0' AFTER `deposit_amount`;
	
update m_deposit_product_term_and_preclosure dp set dp.post_interest_as_per_financial_year=1;	
	
ALTER TABLE `m_calendar`
	ADD COLUMN `savings_id` BIGINT(20) NULL DEFAULT NULL AFTER `lastmodified_date`;
	
INSERT INTO `m_calendar` (`title`, `start_date`, `calendar_type_enum`, `repeating`, `recurrence`,`savings_id`) select CONCAT('interest_posting_',sa.id) , DATE_SUB(DATE_SUB(DATE_ADD(sa.activatedon_date,INTERVAL if(if(config.enabled =1, config.value,1)< MONTH(sa.activatedon_date),12-MONTH(sa.activatedon_date)+if(config.enabled =1, config.value,1),if(config.enabled =1, config.value,1)-MONTH(sa.activatedon_date)) MONTH), INTERVAL 1 YEAR), INTERVAL DAYOFMONTH(sa.activatedon_date)-1  DAY)  as date,5,1,'FREQ=MONTHLY',sa.id  from m_savings_account sa inner join c_configuration config on config.name = 'financial-year-beginning-month' where sa.interest_posting_period_enum = 4 and sa.status_enum = 300;

INSERT INTO `m_calendar` (`title`, `start_date`, `calendar_type_enum`, `repeating`, `recurrence`,`savings_id`) select CONCAT('interest_posting_',sa.id) , DATE_SUB(DATE_SUB(DATE_ADD(sa.activatedon_date,INTERVAL if(if(config.enabled =1, config.value,1)< MONTH(sa.activatedon_date),12-MONTH(sa.activatedon_date)+if(config.enabled =1, config.value,1),if(config.enabled =1, config.value,1)-MONTH(sa.activatedon_date)) MONTH), INTERVAL 1 YEAR), INTERVAL DAYOFMONTH(sa.activatedon_date)-1  DAY) as date,5,1,'FREQ=MONTHLY;INTERVAL=3',sa.id  from m_savings_account sa inner join c_configuration config on config.name = 'financial-year-beginning-month' where sa.interest_posting_period_enum = 5 and sa.status_enum = 300;


INSERT INTO `m_calendar` (`title`, `start_date`, `calendar_type_enum`, `repeating`, `recurrence`,`savings_id`) select CONCAT('interest_posting_',sa.id) , DATE_SUB(DATE_SUB(DATE_ADD(sa.activatedon_date,INTERVAL if(if(config.enabled =1, config.value,1)< MONTH(sa.activatedon_date),12-MONTH(sa.activatedon_date)+if(config.enabled =1, config.value,1),if(config.enabled =1, config.value,1)-MONTH(sa.activatedon_date)) MONTH), INTERVAL 1 YEAR), INTERVAL DAYOFMONTH(sa.activatedon_date)-1  DAY) as date,5,1,'FREQ=MONTHLY;INTERVAL=6',sa.id  from m_savings_account sa inner join c_configuration config on config.name = 'financial-year-beginning-month' where sa.interest_posting_period_enum = 6 and sa.status_enum = 300;


INSERT INTO `m_calendar` (`title`, `start_date`, `calendar_type_enum`, `repeating`, `recurrence`,`savings_id`) select CONCAT('interest_posting_',sa.id) , DATE_SUB(DATE_SUB(DATE_ADD(sa.activatedon_date,INTERVAL if(if(config.enabled =1, config.value,1)< MONTH(sa.activatedon_date),12-MONTH(sa.activatedon_date)+if(config.enabled =1, config.value,1),if(config.enabled =1, config.value,1)-MONTH(sa.activatedon_date)) MONTH), INTERVAL 1 YEAR), INTERVAL DAYOFMONTH(sa.activatedon_date)-1  DAY) as date,5,1,'FREQ=YEARLY;INTERVAL=1',sa.id  from m_savings_account sa inner join c_configuration config on config.name = 'financial-year-beginning-month' where sa.interest_posting_period_enum = 7 and sa.status_enum = 300;

INSERT INTO `m_calendar_instance` (`calendar_id`, `entity_id`, `entity_type_enum`) select mc.id,mc.savings_id,5 from m_calendar mc where mc.savings_id is not null;

ALTER TABLE `m_calendar`
	DROP COLUMN `savings_id`;	