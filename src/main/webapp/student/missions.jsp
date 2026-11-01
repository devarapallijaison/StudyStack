<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,java.util.Map,com.studystack.model.Mission,com.studystack.model.MissionSubmission,com.studystack.util.HtmlUtil" %>
<%
    List<Mission> missions = (List<Mission>) request.getAttribute("missions");
    Map<Integer, MissionSubmission> attempts = (Map<Integer, MissionSubmission>) request.getAttribute("attempts");
    String selectedCategory = (String) request.getAttribute("selectedCategory");
    String selectedDifficulty = (String) request.getAttribute("selectedDifficulty");
    String selectedStatus = (String) request.getAttribute("selectedStatus");
%>
<!doctype html><html lang="en" data-theme="dark"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Missions | StudyStack</title><script src="<%= request.getContextPath() %>/assets/js/theme.js"></script><link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/main.css"></head>
<body><jsp:include page="/includes/header.jsp"/><div class="app-layout"><jsp:include page="/includes/sidebar.jsp"/><main class="content">
    <div class="page-head"><div class="eyebrow">Practice library</div><h1>Choose your next mission.</h1><p>Short challenges to sharpen your Java, SQL, and problem-solving skills.</p></div>
    <form class="form-grid" method="get" action="<%= request.getContextPath() %>/student/missions">
        <label>Category<select name="category"><option value="">All categories</option><option value="Java" <%= "Java".equals(selectedCategory) ? "selected" : "" %>>Java</option><option value="SQL" <%= "SQL".equals(selectedCategory) ? "selected" : "" %>>SQL</option><option value="DSA" <%= "DSA".equals(selectedCategory) ? "selected" : "" %>>DSA</option></select></label>
        <label>Difficulty<select name="difficulty"><option value="">All difficulties</option><option value="Easy" <%= "Easy".equals(selectedDifficulty) ? "selected" : "" %>>Easy</option><option value="Medium" <%= "Medium".equals(selectedDifficulty) ? "selected" : "" %>>Medium</option><option value="Hard" <%= "Hard".equals(selectedDifficulty) ? "selected" : "" %>>Hard</option></select></label>
        <label>Status<select name="status"><option value="">All statuses</option><option value="available" <%= "available".equals(selectedStatus) ? "selected" : "" %>>Available</option><option value="completed" <%= "completed".equals(selectedStatus) ? "selected" : "" %>>Completed</option><option value="attempted" <%= "attempted".equals(selectedStatus) ? "selected" : "" %>>Attempted</option></select></label>
        <button class="button primary" type="submit">Apply filters</button><a class="button" href="<%= request.getContextPath() %>/student/missions">Clear</a>
    </form>
    <% if (missions.isEmpty()) { %><div class="empty">No missions match these filters. Try another selection.</div><% } else { %><div class="grid mission-grid">
        <% for (Mission mission : missions) { MissionSubmission attempt = attempts.get(mission.getMissionId()); String state = attempt == null ? "Available" : (attempt.isCorrect() ? "Completed" : "Attempted"); %>
        <article class="card mission-card"><span class="eyebrow"><%= HtmlUtil.escape(mission.getCategory()) %></span><h3><%= HtmlUtil.escape(mission.getTitle()) %></h3><p><%= HtmlUtil.escape(mission.getDescription()) %></p><div class="card-bottom"><span class="badge"><%= HtmlUtil.escape(mission.getDifficulty()) %></span><span class="badge <%= attempt != null && attempt.isCorrect() ? "success" : "" %>"><%= state %></span><span class="xp">+<%= mission.getXpReward() %> XP</span><a class="button" href="<%= request.getContextPath() %>/student/mission?id=<%= mission.getMissionId() %>"><%= attempt == null ? "Start mission" : "View mission" %></a></div></article>
        <% } %>
    </div><% } %>
</main></div><jsp:include page="/includes/footer.jsp"/></body></html>
