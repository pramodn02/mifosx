update stretchy_report_parameter 
set report_parameter_name='account'
where report_id = (select stretchy_report.id from stretchy_report where report_name='GeneralLedgerReport')
and parameter_id=(select p.id from stretchy_parameter p where parameter_name='SelectGLAccountNO');


update stretchy_report_parameter
set report_parameter_name='fromDate'
where report_id =(select stretchy_report.id from stretchy_report where report_name='GeneralLedgerReport')
and parameter_id=(select p.id from stretchy_parameter p where parameter_name='startDateSelect');

update stretchy_report_parameter
set report_parameter_name='toDate'
where report_id=(select stretchy_report.id from stretchy_report where report_name='GeneralLedgerReport')
and parameter_id=(select p.id from stretchy_parameter p where parameter_name='endDateSelect');

update stretchy_report_parameter
set report_parameter_name='branch'
where report_id=(select stretchy_report.id from stretchy_report where report_name='GeneralLedgerReport')
and parameter_id=(select p.id from stretchy_parameter p where parameter_name='OfficeIdSelectOne');