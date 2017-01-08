var express = require('express');
var mysql = require("mysql");
var app = express();
var fs = require("fs");
var myParser = require("body-parser");
var cors = require('cors');

app.use(cors());
app.use(myParser.json());

//connection to dataBase
var con = mysql.createConnection({
	  host: "localhost",
	  user: "root",
	  password: "root",
	  database: "users"
	});

con.connect(function(err){
	  if(err){
	    console.log('Error connecting to Db');
	    return;
	  }
	  console.log('Connection established');
});

function mysqlDate(date){
    date = date || new Date();
    return date.toISOString().split('T')[0];
}

//adding a new User
app.post('/addUser', function (req, res) {
	var mail = req.body.EMail;

	con.query('SELECT EMail FROM users WHERE EMail = "' + mail + '"' ,function(err, rows){
		//TODO Error-Handling
		if (rows.length !== 0){
			res.status(500).send("InternalServerError");
		} else {
			//TODO jeden neuen User automatisch der Group "public" hinzufügen
		  con.query('INSERT INTO users SET ?', req.body, function(err,result){
			  //TODO Error-Handling
			  con.query('INSERT INTO users.group (Name, Participant) VALUES ("' +
					  'public", "' + mail + '")', function(err,result){
					//TODO Error-Handling
				  console.log('Last inserted User:', req.body.EMail); 
				  res.status(200).send("OK"); 
			});
		  });
		} 
	});
});


//login
app.post('/login', function (req, res) {
	var user = req.body;
	var mail = req.body.EMail;
	var pw = req.body.Password;

	console.log(req.body);

	con.query('SELECT * FROM users WHERE EMail = "' + mail + '" AND Password = "' + pw + '"' ,function(err, rows){
		//TODO Error-Handling
		if (rows.length !== 0){
			//console.log("User gefunden!");
			user.Name = rows[0].Name;
			res.status(200).send(user);
		} else {
			//console.log("User nicht gefunden!");
			res.status(401).send("Unauthorized"); 
		}
	});
});

//create new Group
app.post('/addGroup', function (req, res) {

	var name = req.body.Name;
	var participants = req.body.Participants;
	var members = false;
	
		for (var i in participants){
			con.query('INSERT INTO users.group (Name, Participant) VALUES ("' +
					 name + '", "' + participants[i].EMail + '")', function(err,result){
				  //TODO Error-Handling				  
			});					
			console.log('Last inserted Group:' + name);
			console.log('Last inserted participant:', participants[i].EMail);
			members = true;
		}
		if (members){
			res.status(200).send("OK"); 
		} else {
			res.status(401).send("Unauthorized"); 
		}	
});

//list all Groups
app.post('/listGroups', function(req, res){
	mail = req.body.EMail;
	
	con.query('SELECT DISTINCT Name FROM users.group WHERE users.group.Participant = "' + mail + '"' ,function(err, rows){
		res.status(200).send(rows);
	});
});

//list all Users
app.post('/listUsers', function(req, res){
	con.query('SELECT * FROM users' ,function(err, rows){
		var userList = [];
		for (var i in rows) {
			var user = {};
			user.Name = rows[i].Name;
			user.Password = rows[i].Password;
			user.EMail = rows[i].EMail;
			userList[i] = user;
		}
		res.status(200).send(userList);
	});	
});

app.post('/createBlogEntity', function(req, res){
	var title = req.body.Title;
	var text = req.body.Text;
	var authorMail = req.body.AuthorMail;
	var authorName	= req.body.AuthorName;
	var date = mysqlDate();
	var groupID = req.body.GroupID;
	
	con.query('INSERT INTO users.blogentity (Title, Text, AuthorMail, AuthorName, Date, GroupID) VALUES ("' +
			 title + '", "' + text + '", "' + authorMail + '", "' + authorName + '", "' + date + 
			 '", "' + groupID + '")', function(err,result){
		//TODO Error-Handling
		console.log("Entity created!");
		res.status(200).send("OK");
	});
});

app.post('/getBlogEntities', function(req, res){
    var mail = req.body.EMail;
    var blogEntities = [];
    var counter = 0;

    con.query('SELECT DISTINCT users.group.Name, users.blogentity.ID, users.blogentity.Title, ' +
        'users.blogentity.Text, users.blogentity.AuthorMail, users.blogentity.AuthorName, users.blogentity.Date, users.blogentity.GroupID ' +
        'FROM users.group JOIN users.users ON users.group.Participant = users.users.EMail JOIN users.blogentity ' +
        'ON users.group.Name = users.blogentity.GroupID ' +
        'WHERE users.users.EMail = "' + mail + '"', function(err, result){

        for (var i in result){
            var entity = {};
            entity.ID = result[i].ID;
            entity.Title = result[i].Title;
            entity.Text = result[i].Text;
            entity.AuthorMail = result[i].AuthorMail;
            entity.AuthorName = result[i].AuthorName;
            entity.Date = result[i].Date;
            entity.GroupID = result[i].GroupID;

            blogEntities[counter] = entity;
            counter++;
        }

        console.log(blogEntities);
        res.status(200).send(blogEntities);
    });
});


//app.post('/countComments', function(req, res){
//	var entities = req.body;
//	
//	
//	
//	for (var i in entities){
//		con.query('SELECT users.blogentity.ID, COUNT(users.comments.ID) AS Anzahl ' +
//				'FROM users.comments JOIN users.blogentity ON users.comments.blogID = ' +
//				'users.blogentity.ID WHERE users.blogentity.ID = '+ entities[i].ID , function(err,result){
//			var comments = [];
//			var comment = {};
//			comment.BlogID = result.ID;
//			comment.Count = result.Anzahl;
//			comments[i] = comment;
//			
//			if (i == entities.length - 1){
//				console.log(comments);
//			}
//		});
//	}
//});

app.post('/addComment', function(req, res){
	var blogID = req.body.BlogID;
	var text = req.body.Text;
	var authorMail = req.body.AuthorMail;
	var authorName = req.body.AuthorName;
	var date = mysqlDate();
	
	con.query('INSERT INTO users.comments (BlogID, Text, AuthorMail, AuthorName, Date) VALUES ("' +
			 blogID + '", "' + text + '", "' + authorMail + '", "' + authorName + '", "' + date + 
			 '")', function(err,result){
		//TODO Error-Handling
		console.log("Comment created!");
		res.status(200).send("OK");
	});	
});

app.post('/listComments', function(req, res){
	var id = req.body.ID;
	
	con.query('SELECT * FROM users.comments WHERE comments.blogID = ' + id, function(err, result){
		res.status(200).send(result);
	});
});


var server = app.listen(8081, function () {

  var host = server.address().address;
  var port = server.address().port;
  console.log("Blog-Server listening at http://%s:%s", host, port);
});

