result = from("OfficeSuppliesManagement").where("osManagementId", parameters.get("osManagementId")).queryOne();
context.OfficeSuppliesManagement = result;