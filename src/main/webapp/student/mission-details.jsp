<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.studystack.model.Mission,com.studystack.model.MissionSubmission,com.studystack.util.HtmlUtil" %>
<% Mission mission = (Mission) request.getAttribute("mission"); MissionSubmission attempt = (MissionSubmission) request.getAttribute("attempt"); String result = (String) request.getAttribute("result"); String csrfToken = (String) request.getAttribute("csrfToken"); %>
<!doctype html><html lang="en" data-theme="dark"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title><%= HtmlUtil.escape(mission.getTitle()) %> | StudyStack</title><script src="<%= request.getContextPath() %>/assets/js/theme.js"></script><link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/main.css"></head>
<body><jsp:include page="/includes/header.jsp"/><div class="app-layout"><jsp:include page="/includes/sidebar.jsp"/><main class="content">
    <p><a href="<%= request.getContextPath() %>/student/missions">← All missions</a></p>
    <div class="page-head"><div class="eyebrow"><%= HtmlUtil.escape(mission.getCategory()) %> · <%= HtmlUtil.escape(mission.getDifficulty()) %></div><h1><%= HtmlUtil.escape(mission.getTitle()) %></h1><p><%= HtmlUtil.escape(mission.getDescription()) %></p><span class="xp">Reward: <%= mission.getXpReward() %> XP</span></div>
    <% if (result != null) { %><div class="alert <%= attempt == null ? "" : (attempt.isCorrect() ? "success" : "error") %>" role="status"><%= HtmlUtil.escape(result) %></div><% } %>
    <% if (attempt != null) { %><section class="card"><h2><%= attempt.isCorrect() ? "Mission completed" : "Attempt recorded" %></h2><p><%= attempt.isCorrect() ? "You earned " + attempt.getXpEarned() + " XP. Great work." : "This mission allows one attempt. Review the topic and try another challenge." %></p><a class="button primary" href="<%= request.getContextPath() %>/student/missions">Browse missions</a></section><% } else { %>
    <section class="card"><h2>Choose your answer</h2><form method="post" action="<%= request.getContextPath() %>/student/submit">
        <input type="hidden" name="csrfToken" value="<%= HtmlUtil.escape(csrfToken) %>"><input type="hidden" name="missionId" value="<%= mission.getMissionId() %>">
        <div class="option-list">
            <label class="option"><input type="radio" name="answer" value="A" required><span><b>A.</b> <%= HtmlUtil.escape(mission.getOptionA()) %></span></label>
            <label class="option"><input type="radio" name="answer" value="B" required><span><b>B.</b> <%= HtmlUtil.escape(mission.getOptionB()) %></span></label>
            <label class="option"><input type="radio" name="answer" value="C" required><span><b>C.</b> <%= HtmlUtil.escape(mission.getOptionC()) %></span></label>
            <label class="option"><input type="radio" name="answer" value="D" required><span><b>D.</b> <%= HtmlUtil.escape(mission.getOptionD()) %></span></label>
        </div><button class="button primary" type="submit">Submit answer</button>
    </form></section><% } %>
</main></div><jsp:include page="/includes/footer.jsp"/></body></html>
