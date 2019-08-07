<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<form style="width: 300px;margin:0 auto;" class="login-form" method="post" id="loginform" name="loginform" action="${request.contextPath}/control/login">
    <div style="border-radius: 5px;">
        <h3 style="text-align: center;font-family: \5FAE\8F6F\96C5\9ED1;color:red;margin:10px;font-size: 16px" class="form-title">您的登录账号已被下线，请重新登录!</h3>
        <div class="form-actions" style="text-align: center">
            <input type="hidden" name="JavaScriptEnabled" value="N"/>
            <a id="changeAccountBtn" class="btn green" href="${request.contextPath}/control/frameLogin?frameLogin=true">确定</a>
        </div>
    </div>
</form>