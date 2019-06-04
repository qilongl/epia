-- Create table 接口表
create table SYS_FUNCTION
(
  id            INTEGER not null,
  objid         VARCHAR2(32),
  fun_name      VARCHAR2(128),
  fun_url       VARCHAR2(128),
  crt_dt        DATE default sysdate,
  crt_psn       VARCHAR2(32),
  upd_dt        DATE default sysdate,
  upd_psn       VARCHAR2(32),
  is_delete     INTEGER,
  fun_source    VARCHAR2(128),
  is_contrl_fun INTEGER,
  source        BLOB
)
tablespace SYSTEM
  pctfree 10
  pctused 40
  initrans 1
  maxtrans 255
  storage
  (
    initial 56K
    next 1M
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table SYS_FUNCTION
  is 'xml接口表';
-- Add comments to the columns 
comment on column SYS_FUNCTION.id
  is '序列';
comment on column SYS_FUNCTION.objid
  is '唯一ID';
comment on column SYS_FUNCTION.fun_name
  is '接口名称';
comment on column SYS_FUNCTION.fun_url
  is '接口URL';
comment on column SYS_FUNCTION.crt_dt
  is '创建时间';
comment on column SYS_FUNCTION.crt_psn
  is '创建人';
comment on column SYS_FUNCTION.upd_dt
  is '更新时间';
comment on column SYS_FUNCTION.upd_psn
  is '更新人';
comment on column SYS_FUNCTION.is_delete
  is '是否删除';
comment on column SYS_FUNCTION.fun_source
  is '接口来源';
comment on column SYS_FUNCTION.is_contrl_fun
  is '是否受管控';
comment on column SYS_FUNCTION.source
  is '接口文件二进制存储';
  

-- Create table 接口校验表
create table SYS_TOKEN_CLOUD
(
  id               NUMBER not null,
  account          VARCHAR2(64),
  token_key        VARCHAR2(128),
  description      VARCHAR2(512),
  status           NUMBER,
  term_type        VARCHAR2(64),
  term_id          VARCHAR2(128),
  start_time       DATE,
  end_time         DATE,
  last_time        DATE,
  tolerant_sec     NUMBER,
  check_ciphertext NUMBER
)
tablespace SYSTEM
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
-- Add comments to the columns 
comment on column SYS_TOKEN_CLOUD.id
  is 'ID';
comment on column SYS_TOKEN_CLOUD.account
  is '账户号';
comment on column SYS_TOKEN_CLOUD.token_key
  is '账户TOKEN值';
comment on column SYS_TOKEN_CLOUD.description
  is '账户说明';
comment on column SYS_TOKEN_CLOUD.status
  is '账户状态';
comment on column SYS_TOKEN_CLOUD.term_type
  is '账户终端类型';
comment on column SYS_TOKEN_CLOUD.term_id
  is '账户终端号';
comment on column SYS_TOKEN_CLOUD.start_time
  is '账户分配时间';
comment on column SYS_TOKEN_CLOUD.end_time
  is '账户过期时间';
comment on column SYS_TOKEN_CLOUD.last_time
  is '最后操作时间';
comment on column SYS_TOKEN_CLOUD.tolerant_sec
  is '服务器容许间隔时间（s）';
comment on column SYS_TOKEN_CLOUD.check_ciphertext
  is '是否验证密文';

-- 接口校验信息
insert into sys_token_cloud (ID, ACCOUNT, TOKEN_KEY, DESCRIPTION, STATUS, TERM_TYPE, TERM_ID, START_TIME, END_TIME, LAST_TIME, TOLERANT_SEC, CHECK_CIPHERTEXT)
values (1, 'easipass', 'token_easipass', '亿通xml解析组件接口校验', 0, 'pc', '192.168.12.150', to_date('12-10-2018', 'dd-mm-yyyy'), to_date('12-10-2020', 'dd-mm-yyyy'), to_date('08-06-2018 11:20:15', 'dd-mm-yyyy hh24:mi:ss'), 50000000, 0);

commit;

-- Create sequence 序列
create sequence SYS_ID
minvalue 1
maxvalue 999999999999999999999
start with 461
increment by 1
cache 20;
  

