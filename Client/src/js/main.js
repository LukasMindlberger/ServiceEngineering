(function(){
  var app = angular.module('blogService',['ui.bootstrap']);
  
  
  app.controller('BlogController', ['$scope', '$http', '$window', function($scope, $http, $window){

    var ip = "http://127.0.0.1";
    var port = ":8081";
    var url = ip+port;


    var blog = this;
    blog.title = "Wi(n)Blog";

    blog.post = {};
    blog.posts = {};
	
    blog.tab = 'login';
	
    blog.selectTab = function(setTab){
      if(setTab == "blog"){
        blog.getPosts();
      }
      blog.tab = setTab;
      console.log(blog.tab)
    };
    
    blog.isSelected = function(checkTab){
      return blog.tab === checkTab;
    };
    


    blog.registrate = function(){
    	if(blog.usernameNew == null || blog.passwordNew == null || blog.password2New == null || blog.emailNew == null ){
    		$window.alert("Please fill in required information!");
    	}else if(blog.passwordNew != blog.password2New){
    		$window.alert("Passwords are not equal!");
    	}else{

            var user = {};
            user.Name = blog.usernameNew;
            user.Password = blog.passwordNew;
            user.EMail = blog.emailNew;

            $http.post(url+'/addUser', user)
                .success(function(data) {
                  console.log(data);

                  $window.alert("Registration was successfull!");
                    blog.usernameNew = "";
                    blog.passwordNew = "";
                    blog.password2New = "";
                    blog.emailNew = "";
                })
                .error(function(data){
                  console.log(data);
                  $window.alert("A user with this email address is already registrated.");
                });

    	}
    };

    blog.currentUser = {};

    blog.login = {};
    blog.userLogin = function() {

      var login = {};
      login.EMail = blog.login.userEmailaddress;
      login.Password = blog.login.userPassword;

      blog.userLoggedIn = false;
      $http.post(url + '/login', login)
          .success(function (data) {
            blog.userLoggedIn = true;
            blog.currentUser = data;
            blog.selectTab("blog");
          })
          .error(function (data) {
            $window.alert("Login failed, please try again!");
          });

      blog.login.userEmailaddress = "";
      blog.login.userPassword = "";
    };

    blog.userLogout = function(){
      blog.currentUser = null;
      blog.userLoggedIn = false;
      blog.tab = 'login'
    };

    blog.members = [];
    blog.allMembers = {};
    blog.getAllMembers = function(){
      $http.post(url+'/listUsers', "")
          .success(function(data){
            blog.allMembers = data;
          });
    };

    blog.addMember = function(newMember){
      blog.members.push(newMember);
      blog.newMember="";
    };

    blog.deleteMember = function(member){
      blog.members.splice(blog.members.indexOf(member),1);
    };

    blog.createGroup = function(){
      var newGroup = {};

      if(blog.newGroupTitle != null && blog.newGroupTitle != "") {
        newGroup.Name = blog.newGroupTitle;
        newGroup.Participants = [];
        for (mem in blog.members) {
          var user2 = {};
          user2.Name = blog.members[mem].Name;
          user2.EMail = blog.members[mem].EMail;
          newGroup.Participants[mem] = user2;
        }

        $http.post(url + '/addGroup', newGroup)
            .success(function (data) {
              $window.alert("Group created!");
                blog.newGroupTitle = "";
                blog.members = [];
            })
            .error(function (data) {
              $window.alert("Group not created, no members!");
            })
      }else{
        $window.alert("Group not created, please fill in a group title!");
      }
    };


    blog.allGroups = {};
    blog.getAllGroups = function(){
        var mail = {};
        mail.EMail = blog.currentUser.EMail;
      $http.post(url + '/listGroups', mail)
          .success(function(data){
            blog.allGroups = data;
          });
    };

    blog.createPostInGroup = "public";
    blog.selectedGroup = function(groupName){
      blog.createPostInGroup = groupName;
    };


    blog.addPost = function(){
      var postToSend = {};
      postToSend.Title = blog.newPostTitle;
      postToSend.Text = blog.newPostBody;
      postToSend.AuthorMail = blog.currentUser.EMail;
      postToSend.AuthorName = blog.currentUser.Name;
      postToSend.Date = String(Date.now());
      postToSend.GroupID = blog.createPostInGroup;
      $http.post(url + '/createBlogEntity', postToSend)
          .success(function(data){
            blog.getPosts();
            $window.alert("Post created!");
            blog.newPostTitle = "";
            blog.newPostBody = "";
          })
          .error(function(data){
            $window.alert("Post not created!");
          });
    };



      blog.comments = {};
      blog.currentPost = {};
    blog.storeCurrentPost = function(post){
        blog.currentPost = post;
        blog.getComments();
    };


    blog.getPosts = function(){
      var mail = {};
      mail.EMail = blog.currentUser.EMail;
      $http.post(url + '/getBlogEntities', mail)
          .success(function(data){
            blog.posts = data;
          })
    };

    blog.status = {
      isopen: false
    };

    blog.status = {
      isopen2: false
    };

    blog.status = {
      isopen3: false
    };

    blog.addComment = function(){
      var comment = {};
      comment.BlogID = blog.currentPost.ID;
      comment.Text = blog.commentText;
      comment.AuthorMail = blog.currentUser.EMail;
        comment.AuthorName = blog.currentUser.Name;
      comment.Date = Date.now();

        $http.post(url + '/addComment', comment)
            .success(function(data){
                $window.alert("Comment added!");
                blog.commentText = "";
                blog.getComments();
            })

    }


      blog.getComments = function(){
          var id = {};
          id.ID = blog.currentPost.ID;
          $http.post(url + '/listComments', id)
              .success(function(data){
                  blog.comments = data;
              })
      }

  }]);
})();