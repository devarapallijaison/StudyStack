<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    int studentCount = (Integer) request.getAttribute("studentCount");
    int missionCount = (Integer) request.getAttribute("missionCount");
    int completedCount = (Integer) request.getAttribute("completedCount");
    int availableCount = (Integer) request.getAttribute("availableCount");
%>
<!doctype html><html lang="en" data-theme="dark"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Admin dashboard | StudyStack</title><script src="<%= request.getContextPath() %>/assets/js/theme.js"></script><link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/main.css"></head>
<body><jsp:include page="/includes/header.jsp"/><div class="app-layout"><jsp:include page="/includes/admin-sidebar.jsp"/><main class="content">
    <div class="page-head"><div class="eyebrow">Administration</div><h1>Platform overview</h1><p>Track the learning library and student activity.</p></div>
    <section class="grid stats-grid"><article class="card"><div class="stat-label">Registered students</div><div class="stat-value"><%= studentCount %></div></article><article class="card"><div class="stat-label">Total missions</div><div class="stat-value"><%= missionCount %></div></article><article class="card"><div class="stat-label">Completed submissions</div><div class="stat-value"><%= completedCount %></div></article><article class="card"><div class="stat-label">Available missions</div><div class="stat-value"><%= availableCount %></div></article></section>
    <section class="section card"><div class="eyebrow">Mission library</div><h2>Keep the practice set useful</h2><p>Create, revise unused questions, or archive missions that should no longer appear to students. Attempts keep their history.</p><a class="button primary" href="<%= request.getContextPath() %>/admin/missions/new">Create a mission</a> <a class="button" href="<%= request.getContextPath() %>/admin/missions">Manage missions</a></section>
</main></div><jsp:include page="/includes/footer.jsp"/></body></html>
