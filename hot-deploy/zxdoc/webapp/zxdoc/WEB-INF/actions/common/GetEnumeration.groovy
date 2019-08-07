
context.enums = from("Enumeration").where("enumTypeId", context.enumTypeId).queryList();
context.enumType = from("EnumerationType").where("enumTypeId", context.enumTypeId).queryOne();
