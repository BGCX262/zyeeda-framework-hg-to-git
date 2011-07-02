-- execute as sysdba

grant select on sys.dba_pending_transactions to <username>;
grant select on sys.pending_trans$ to <username>;
grant select on sys.dba_2pc_pending to <username>;
grant execute on sys.dbms_system to <username>;