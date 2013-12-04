<div id="thread.watch.notify" class="jive-info-box" style="display:none"></div>

<div id="jive-view-memos-container">
    <@s.action name="view-memos" executeResult="true">
        <@s.param name="containerType" value="${container.objectType?c}"/>
        <@s.param name="containerID" value="${container.ID?c}"/>
    </@s.action>
</div>
