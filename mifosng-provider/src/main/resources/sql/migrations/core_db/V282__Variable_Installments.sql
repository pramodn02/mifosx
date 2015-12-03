ALTER TABLE `m_product_loan`
ADD COLUMN `allow_variabe_installments` BIT(1) NOT NULL DEFAULT 0 AFTER `is_linked_to_floating_interest_rates` ;

CREATE TABLE `m_product_loan_variable_installment_config` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`loan_product_id` BIGINT(20) NOT NULL,
	`minimum_gap` INT(4) NOT NULL,
	`maximum_gap` INT(4) NOT NULL,
	`minimum_installment_amount` DECIMAL(19,6) NOT NULL DEFAULT 0,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK_mappings_m_variable_product_loan_id` FOREIGN KEY (`loan_product_id`) REFERENCES `m_product_loan` (`id`)	
);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'CREATESCHEDULEEXCEPTIONS_LOAN', 'LOAN', 'CREATESCHEDULEEXCEPTIONS', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'CREATESCHEDULEEXCEPTIONS_LOAN_CHECKER', 'LOAN', 'CREATESCHEDULEEXCEPTIONS_CHECKER', 0);