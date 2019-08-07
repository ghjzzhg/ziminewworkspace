<div align="center">
    <#if data?has_content>
        <#if data.isyLibrary?default(false)>
            <#if data.isFile?default(false)>
                <p>${data.fileName?default("")}</p>
                    <#if !data.isyHistory>
                        <p >所需积分：<b class="ml-5">${data.libraryScore?default("")}</b>&nbsp;您持有：<span ><b class="ml-5">${data.userScoreNo?default()}</b></span>积分</p>
                        <p >
                        <#if !data.isDownLoad>
                            <span class="ps">积分不足，无法下载</span>
                        <#else>
                            <span class="ps"><#if data.downloadSelf?has_content && data.downloadSelf>用户上传，不需积分。</#if>请点击“
                                <a href="/content/control/downloadScoreFile?scoreId=${data.libraryId?default("")}&modelId=library&externalLoginKey=${externalLoginKey}">
                                下载文件</a>”链接，下载资源文件</span>
                        </#if>
                    <#else>
                        <p >已下载过该文件，不需要使用积分下载</p>
                        <p >
                        <span class="ps">请点击“
                                <a href="/content/control/downloadScoreFile?scoreId=${data.libraryId?default("")}&modelId=library&externalLoginKey=${externalLoginKey}">
                                下载文件</a>”链接，下载资源文件</span>
                    </#if>
                </p>
            <#else>
            <p >文件不存在</p>
            </#if>
        <#else>
            <p >该资源已被删除，请刷新页面</p>
        </#if>
    </#if>
</div>