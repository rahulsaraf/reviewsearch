<!DOCTYPE html>
<html>
<head>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">

<title>Welcome to Review Search</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
.main {
	top: 4.5em;
	background:white;
}

.query {
	width: 400px;
	height:25px;
	text-align: left;
	overflow-y: hidden;
	overflow-x: scroll;
}

.search {
	width: 100px;
	color:blue;
}

.local {
	position: fixed;
	width: 31%;
	height: 90%;
	padding: 0px 10px;
	/* border: 1px solid black; */
	overflow-y: scroll;
	text-align:center;
	/*text-decoration:underline;*/
	}

.google {
	position: fixed;
	margin-left: 33%;
	width: 31%;
	height: 90%;
	padding: 0px 10px;
	/* border: 1px solid black; */
	overflow-y: scroll;
	/* color:Blue; */
	text-align:center;
	/*text-decoration:underline;*/
}

.bing {
	position: fixed;
	margin-left: 66%;
	width: 31%;
	height: 90%;
	padding: 0px 10px;
	/* border: 1px solid black; */ 
	overflow-y: scroll;
	/* color:orange; */
	text-align:center;
	/*text-decoration:underline;*/
}

.center-block {
  display: block;
  margin-left: auto;
  margin-right: auto;
}
body {
    font-family: "Palatino Linotype", "Book Antiqua", Palatino, serif;
}
#x { width: 250px; }
</style>


</head>
<body>
	
	<div align="center"><h1>REVIEW SEARCH</h1></div>
	<div align="center" class="main">
		<form class="form-inline" action="search" method="post">
		 <div class="form-group">
			<label class="sr-only">Search:</label> 
			<input id = "x" class="form-control" type="text" name="searchText" value="${searchText}">
		</div> 
			<input class="btn btn-primary" type="submit" name="search" value="Search">
		</form>
	</div>

	<div class="local">
		<h2>Local Search</h2>
		<h4><strong>Positive:${posReviews} </strong><strong>Negative:${negReviews} </strong><strong>Neutral:${neuReviews}</strong></h4>
					<c:forEach items="${records}" var="doc" varStatus="recordIndex">
						<h4>
						<strong>(${doc.sentiment})<a href='<c:out value="${doc.urlOfDoc}"/>'><c:out
												value="${doc.titleOfDoc}" /></a></strong>
						</h4>
						<a href='<c:out value="${doc.urlOfDoc}"/>'><c:out
										value="${doc.urlOfDoc}" /></a>
						<br>
										"${doc.docContents}
					</c:forEach>
	</div>

	<div class="google">
		<h2>Google Search</h2>
		<c:forEach items="${googleRecords}" var="doc" varStatus="recordIndex">
						<h4>
						<strong><a href='<c:out value="${doc.urlOfDoc}"/>'><c:out
												value="${doc.titleOfDoc}" /></a></strong>
						</h4>
						<a href='<c:out value="${doc.urlOfDoc}"/>'><c:out
										value="${doc.urlOfDoc}" /></a>
						<br>
										"${doc.docContents}
		</c:forEach>
	</div>

	<div class="bing">
		<h2>Bing Search</h2>
		<c:forEach items="${bingRecords}" var="doc" varStatus="recordIndex">
						<h4>
						<strong><a href='<c:out value="${doc.urlOfDoc}"/>'><c:out
												value="${doc.titleOfDoc}" /></a></strong>
						</h4>
						<a href='<c:out value="${doc.urlOfDoc}"/>'><c:out
										value="${doc.urlOfDoc}" /></a>
						<br>
										"${doc.docContents}
		</c:forEach>
	</div>
</body>
</html>