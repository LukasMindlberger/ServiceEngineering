(function(){
  var app = angular.module('blogService',['ui.bootstrap']);
  
  
  app.controller('BlogController', ['$scope', '$http', '$window', function($scope, $http, $window){

    var ip = "http://127.0.0.1";
    var port = ":8081";
    var url = ip+port;



    
    var blog = this;
    blog.title = "Really really cool blog service app";
    
    blog.posts = {};
    $http.get('example.json').success(function(data){
      blog.posts = data;
    });
	
    blog.tab = 'login';
	
    blog.selectTab = function(setTab){
      blog.tab = setTab;
      console.log(blog.tab)
    };
    
    blog.isSelected = function(checkTab){
      return blog.tab === checkTab;
    };
    
    blog.post = {};
    blog.addPost = function(){
      var post =




      blog.post.createdOn = Date.now();
      blog.post.comments = [];
      blog.post.likes = 0;
      blog.posts.unshift(this.post);
      blog.tab = 0;
      blog.post ={};
      //to send
    };



    
    blog.members=[];

    blog.allMembers = ["Robert", "Christian", "Thomas"];
    blog.getAllMembers = function(){

    };
    
    blog.addMember = function(newMember){
    	blog.members.push(newMember);
    	blog.newMember="";
    	//to send
    };


    blog.deleteMember = function(member){
        blog.members.splice(blog.members.indexOf(member),1);
    };
    


    blog.registrate = function(username, password, password2, emailaddress){
    	if(username == null || password == null || password2 == null || emailaddress == null ){
    		$window.alert("Please fill in required information!");
    	}else if(password != password2){
    		$window.alert("Passwords are not equal!");
    	}else{

            var user = {};
            user.Name = username;
            user.Password = password;
            user.EMail = emailaddress;

            $http.post(url+'/addUser', user)
                .success(function(data) {
                  console.log(data);
                  $window.alert("Registration was successfull!");
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
            blog.tab = 'blog';
            blog.userLoggedIn = true;
            blog.currentUser = data;
          })
          .error(function (data) {
            $window.alert("Login failed, please try again!");
          });
    };


    blog.userLogout = function(){
      blog.currentUser = null;
      blog.userLoggedIn = false;
    }

    blog.addGroup = function(){

    };











    blog.status = {
      isopen: false
    };

    blog.status = {
      isopen2: false
    };

    

  }]);
  
  app.controller('CommentController', function(){
    this.comment = {};
    this.addComment = function(post){
      this.comment.createdOn = Date.now();
      post.comments.push(this.comment);
      this.comment ={};
    };
  });
 
})();