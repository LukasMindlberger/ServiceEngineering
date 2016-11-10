/*

Simple blog front end demo in order to learn AngularJS - You can add new posts, add comments, and like posts.

*/

(function(){
  var app = angular.module('blogService',[]);
  
  
  app.controller('BlogController', ['$scope', '$http', function($scope, $http){
    
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
      //to push
    };   

    blog.registrate = function(){
      blog.username = "";
      blog.password ="";
      blog.email = "";
      //to push
    }
    
    blog.members=["test"];
    
    blog.addMember = function(newMember){
    	
    	blog.member.push(newMember);
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