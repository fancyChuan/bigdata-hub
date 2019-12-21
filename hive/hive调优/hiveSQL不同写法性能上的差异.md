## hiveSQL不同写法性能上的差异


```
from (SELECT id FROM ods_touna.dw_borrow_tender WHERE tender_type in (0,1,7) and wait_account >0) a
JOIN (select  from ods_touna.dw_borrow_collection WHERE status=0) b on a.id=b.tender_id 



from (SELECT id FROM ods_touna.dw_borrow_tender WHERE tender_type in (0,1,7) and wait_account >0) a
JOIN ods_touna.dw_borrow_collection b on a.id=b.tender_id 
where status=0c
```