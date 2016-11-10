(function(){
  var app = angular.module('blogService',[]);
  
  
  app.controller('BlogController', ['$scope', '$http', '$window', function($scope, $http, $window){
    
    var blog = this;
    blog.title = "Really really cool blog service app";
    
    blog.posts = {};
    $http.get('example.json').success(function(data){
      blog.posts = data;
    });
	
    blog.tab = 'blog';
	
    blog.selectTab = function(setTab){
      blog.tab = setTab;
      console.log(blog.tab)
    };
    
    blog.isSelected = function(checkTab){
      return blog.tab === checkTab;
    };
    
    blog.post = {};
    blog.addPost = function(){
      blog.post.createdOn = Date.now();
      blog.post.comments = [];
      blog.post.likes = 0;
      blog.posts.unshift(this.post);
      blog.tab = 0;
      blog.post ={};
      //to send
    };   

    blog.registrate = function(){
      blog.username = "";
      blog.password ="";
      blog.email = "";
      //to send
    }
    
    blog.members=[];
    
    blog.addMember = function(newMember){
    	blog.members.push(newMember);
    	blog.newMember="";
    	//to send
    }
    
    
    blog.user="";
    
    blog.registrate = function(){
    	if(blog.newUsername == null || blog.newPassword == null || blog.newPassword2 == null || blog.newEmail == null ){
    		$window.alert("Please fill in required information!");
    	}else if(blog.newPassword != blog.newPassword2){
    		$window.alert("Passwords are not equal!");
    	}else{
    		$window.alert("Registration complete!");
    	}
    }
    

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