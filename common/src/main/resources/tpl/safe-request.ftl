<div>
    <h4>${data.method} ${data.url}</h4>
    <#if data.headers??>
        <h4>Headers</h4>
        <#list data.headers as name, value>
            <#if name?lower_case != "authorization" && name?lower_case != "cookie">
                <div>${name}: ${value}</div>
            </#if>
        </#list>
    </#if>
    <#if data.body??><h4>Body</h4><pre>${data.body}</pre></#if>
</div>
