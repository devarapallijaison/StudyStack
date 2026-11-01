<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.studystack.model.Mission,com.studystack.util.CsrfToken,com.studystack.util.HtmlUtil" %>
<%
    Mission mission = (Mission) request.getAttribute("mission");
    if (mission == null) mission = new Mission();
    boolean editing = mission.getMissionId() > 0;
    String error = (String) request.getAttribute("error");
    String csrfToken = CsrfToken.getOrCreate(session);
%>
<!doctype html><html lang="en" data-theme="dark"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title><%= editing ? "Edit" : "Create" %> mission | StudyStack</title><script src="<%= request.getContextPath() %>/assets/js/theme.js"></script><link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/main.css"></head>
<body><jsp:include page="/includes/header.jsp"/><div class="app-layout"><jsp:include page="/includes/admin-sidebar.jsp"/><main class="content">
    <div class="page-head"><div class="eyebrow">Mission library</div><h1><%= editing ? "Edit mission" : "Create a mission" %></h1><p>Write a clear question with four distinct answers and one correct choice.</p></div>
    <% if (error != null) { %><div class="alert error" role="alert"><%= HtmlUtil.escape(error) %></div><% } %>
    <% if (editing && !mission.isActive()) { %><div class="alert error">This mission is archived and cannot be edited.</div><% } %>
    <form class="card" method="post" action="<%= request.getContextPath() %>/admin/missions">
        <input type="hidden" name="csrfToken" value="<%= HtmlUtil.escape(csrfToken) %>"><input type="hidden" name="missionId" value="<%= editing ? mission.getMissionId() : "" %>">
        <div class="grid two-col">
            <label>Mission title<input name="title" required maxlength="150" value="<%= HtmlUtil.escape(mission.getTitle()) %>"></label>
            <label>Category<select name="category" required><option value="">Choose category</option><option value="Java" <%= "Java".equals(mission.getCategory()) ? "selected" : "" %>>Java</option><option value="SQL" <%= "SQL".equals(mission.getCategory()) ? "selected" : "" %>>SQL</option><option value="DSA" <%= "DSA".equals(mission.getCategory()) ? "selected" : "" %>>DSA</option></select></label>
            <label>Difficulty<select name="difficulty" required><option value="">Choose difficulty</option><option value="Easy" <%= "Easy".equals(mission.getDifficulty()) ? "selected" : "" %>>Easy</option><option value="Medium" <%= "Medium".equals(mission.getDifficulty()) ? "selected" : "" %>>Medium</option><option value="Hard" <%= "Hard".equals(mission.getDifficulty()) ? "selected" : "" %>>Hard</option></select></label>
            <label>XP reward<input name="xpReward" type="number" min="1" max="10000" required value="<%= mission.getXpReward() > 0 ? mission.getXpReward() : "" %>"></label>
        </div>
        <label style="margin-top:16px">Description<textarea name="description" rows="4" required><%= HtmlUtil.escape(mission.getDescription()) %></textarea></label>
        <div class="grid two-col" style="margin-top:16px"><label>Option A<input name="optionA" required maxlength="255" value="<%= HtmlUtil.escape(mission.getOptionA()) %>"></label><label>Option B<input name="optionB" required maxlength="255" value="<%= HtmlUtil.escape(mission.getOptionB()) %>"></label><label>Option C<input name="optionC" required maxlength="255" value="<%= HtmlUtil.escape(mission.getOptionC()) %>"></label><label>Option D<input name="optionD" required maxlength="255" value="<%= HtmlUtil.escape(mission.getOptionD()) %>"></label></div>
        <label style="margin-top:16px">Correct answer<select name="correctAnswer" required><option value="">Choose answer</option><option value="A" <%= "A".equals(mission.getCorrectAnswer()) ? "selected" : "" %>>A</option><option value="B" <%= "B".equals(mission.getCorrectAnswer()) ? "selected" : "" %>>B</option><option value="C" <%= "C".equals(mission.getCorrectAnswer()) ? "selected" : "" %>>C</option><option value="D" <%= "D".equals(mission.getCorrectAnswer()) ? "selected" : "" %>>D</option></select></label>
        <div style="display:flex;gap:10px;margin-top:22px"><button class="button primary" type="submit" <%= editing && !mission.isActive() ? "disabled" : "" %>><%= editing ? "Save changes" : "Create mission" %></button><a class="button" href="<%= request.getContextPath() %>/admin/missions">Cancel</a></div>
    </form>
</main></div><jsp:include page="/includes/footer.jsp"/></body></html>
