<h4><@s.text name="settings.actions.gtitle" /></h4>
<ul>
  <#if !JiveContainerPermHelper.isBannedFromPosting()>
    <li><a href="<@s.url action='choose-container' method='input'/>?contentType=${memoType?c}"><span class="jive-icon-med jive-icon-memo-med"></span><@s.text name="memo.create.title" /></a></li>
  </#if>
</ul>
