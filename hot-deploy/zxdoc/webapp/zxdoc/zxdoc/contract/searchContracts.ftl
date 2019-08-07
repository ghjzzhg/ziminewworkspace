


<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">签约合同</span>
        </div>
        <div class="tools"></div>
    </div>
    <div class="portlet-body">
        <div class="row">
            <div class="form-group col-sm-2">
                <label class="control-label">参与方</label>

                <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-user"></i>
                        </span>
                    <input type="text" id="contractA" name="contractA" class="form-control"/>
                </div>
            </div>
            <div class="form-group col-sm-2">
                <label class="control-label">开始日期</label>

                <div class="input-group">
                    <span class="input-group-addon">
                        <i class="fa fa-calendar"></i>
                    </span>
                    <input type="text" class="form-control" name="startDate" id="startDate"
                           onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})"/>
                </div>
            </div>
            <div class="form-group col-sm-2">
                <label class="control-label">结束日期</label>

                <div class="input-group">
                    <span class="input-group-addon">
                        <i class="fa fa-calendar"></i>
                    </span>
                    <input type="text" class="form-control" name="dateClose" id="dateClose"
                           onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})"/>
                </div>
            </div>
        </div>
        <div class="form-group ">
            <div class="margiv-top-10">
                <a href="javascript:;" class="btn green"
                   onclick="searchContracts('NewContract?contractId={{contractId}}')">查找</a>
            </div>
        </div>
        </div>
