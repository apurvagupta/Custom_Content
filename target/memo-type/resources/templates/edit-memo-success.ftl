<html>
<head>
    <title>
        New Memo: ${memo.title}
    </title>

    <meta name="nosidebar" content="true" />

    <content tag="breadcrumb">
        <#if (community?exists)>
            <@s.action name="community-breadcrumb" executeResult="true" ignoreContextParams="true">
                <@s.param name="community" value="${community.ID?c}" />
            </@s.action>
        <#else>
            <@s.action name="community-breadcrumb" executeResult="true" ignoreContextParams="true" />
        </#if>
    </content>
</head>
<body>


<!-- BEGIN header & intro  -->
<div id="jive-body-intro">
    <div id="jive-body-intro-content">
        <h1>
            <span class="jive-icon-big jive-icon-discussion"></span>
            Create Memo
        </h1>
        <p>

            <#if (memoModerated)>

                    Your memo has been placed in a moderation queue and will be posted
                    when it is approved by a moderator.

            <#else>

                    Your memo was posted successfully.

            </#if>

        </p>
    </div>
</div>
<!-- END header & intro -->


<!-- BEGIN main body -->
<div id="jive-body-main">


    <!-- BEGIN main body column -->
    <div id="jive-body-maincol-container">
    <div id="jive-body-maincol">


        <ul>

                    <li><#-- Go to: the community you posted in -->
                        <@s.text name="global.go_to" /><@s.text name="global.colon" />
                        <a href="<@s.url value='${JiveResourceResolver.getJiveObjectURL(container)}'/>"
                        ><@s.text name="global.the_space_you_posted_in" /></a>
                    </li>

                    <li><#-- Go to: the main forum page -->
                        <@s.text name="global.go_to" /><@s.text name="global.colon" />
                        <a id="jive-main-community" href="index.jspa"><@s.text name="global.the_main_community_page" /></a>
                    </li>

        </ul>

    </div>
    </div>
    <!-- END main body column -->


</div>
<!-- END main body -->


</body>
</html>