<#macro memoList memos limit=-1>
<div class="jive-table">
    <table class="jive-memo-list-table" cellpadding="0" cellspacing="0" border="0">
        <thead>
            <tr>
                <th class="jive-table-head-author" colspan="2"><@s.text name="global.author" /></th>
                <th class="jive-table-head-subject" colspan="2"><@s.text name="global.subject" /></th>
                <th class="jive-table-head-modified"><@s.text name="global.last_modified" /></th>
            </tr>
        </thead>
    
        <tbody>
            <#list memos as memo>
            <#if (limit > 0 && memo_index >= limit)>
                <#break/>
            </#if>
            <tr class="jive-table-row-<#if (memo_index % 2 == 0)>odd<#else>even</#if>">
                <td class="jive-table-cell-avatar"><@jive.userAvatar user=memo.user size=22/></td>

                <td class="jive-table-cell-author"><@jive.userDisplayNameLink user=memo.user/></td>

                <td class="jive-table-cell-type"><img class="${SkinUtils.getJiveObjectCss(memo, 1)}" src="<@s.url value='/images/transparent.png'/>" alt="" /></td>
                <td class="jive-table-cell-subject">
                    <a href="<@s.url value='/memos/${memo.ID?c}'/>">
                        ${action.renderSubjectToText(memo)}
                    </a>
                    <span><@s.text name='global.in' /> <a href="<@s.url value='${JiveResourceResolver.getJiveObjectURL(memo.jiveContainer)}'/>">${memo.jiveContainer.name?html}</a></span>
                </td>
                <td class="jive-table-cell-modified"><span><a href="<@s.url value='/memos/${memo.ID?c}'/>">${StringUtils.getTimeFromLong(memo.modificationDate.time?long, 1)}</a></span>
                    <span><@s.text name="doc.authrs.by.label" />
                        <@jive.userDisplayNameLink user=memo.user/>
                    </span>
                </td>
            </tr>
            </#list>
        </tbody>
    </table>
</div>
</#macro>
