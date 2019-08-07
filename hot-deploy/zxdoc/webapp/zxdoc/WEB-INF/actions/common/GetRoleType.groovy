package common

/**
 * Created by Administrator on 2016/12/6.
 * 该groovy用于获取case成员角色
 */
context.enums = from("RoleType").where("parentTypeId", context.roleTypeId).queryList();
context.enumType = from("RoleType").where("roleTypeId", "CASE_PERSON_ROLE").queryOne();

