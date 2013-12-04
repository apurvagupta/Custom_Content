<#include "/plugins/memo-type/resources/templates/memo-macros.ftl">

<html>
<head>
    <title>${community.name?html} <@s.text name="main.memos.memos.title" /></title>

    <content tag="breadcrumb">
        <@s.action name="community-breadcrumb" executeResult="true" ignoreContextParams="true">
            <@s.param name="container" value="${community.ID?c}" />
            <@s.param name="containerType" value="${community.objectType?c}" />
        </@s.action>
    </content>
</head>

<body class="jive-body-memos">


<!-- BEGIN header & intro  -->
<div id="jive-body-intro">
    <div id="jive-body-intro-content">
        <div id="jive-body-intro-main-hdr">
            <h1><span class="jive-icon-big jive-icon-memo-big"></span><@s.text name="main.memos.memos.title" /></h1>
        </div>
    </div>
</div>
<!-- END header & intro -->


<!-- BEGIN main body -->
<div id="jive-body-main">

    <!-- BEGIN main body column -->
    <div id="jive-body-maincol-container">
        <div id="jive-body-maincol">
            <div id="jive-view-memos-container">
                <@s.action name="view-memos" executeResult="true" />
            </div>
        </div>
    </div>
    <!-- END main body column -->



    <!-- BEGIN sidebar column -->
    <div id="jive-body-sidebarcol-container">
        <div id="jive-body-sidebarcol">

            <#if !user.anonymous>
            <!-- BEGIN sidebar box 'ACTIONS' -->
            <div class="jive-sidebar jive-sidebar-actions">
                <#if !JiveContainerPermHelper.isBannedFromPosting()>
                <div class="jive-sidebar-header"></div>
                <div class="jive-sidebar-body">
                    <h4><@s.text name="main.memos.actions.gtitle" /></h4>
                    <ul class="jive-icon-list">
                         <li><a href="<@s.url action='choose-container' method='input'/>?contentType=${memoType?c}"><span class="jive-icon-med jive-icon-memo-med"></span><@s.text name="memo.create.title" /></a></li>
                    </ul>
                </div>
                </#if>
            </div>
            <!-- END sidebar box 'ACTIONS' -->
            </#if>

            <@s.action name="tag-cloud" executeResult="true" ignoreContextParams="true">
                <@s.param name="community" value="${community.ID?c}"/>
                <@s.param name="taggableTypes" value="${memoType?c}"/>
                <@s.param name="recursive" value="'true'"/>
                <@s.param name="numResults" value="'15'"/>
            </@s.action>

        </div>
    </div>
    <!-- END sidebar column -->


</div>
<!-- END main body -->


</body>
</html>
