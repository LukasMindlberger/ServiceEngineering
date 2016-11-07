/*

 Simple blog front end demo in order to learn AngularJS - You can add new posts, add comments, and like posts.

 */

(function () {
    var app = angular.module('blogService', []);


    app.controller('BlogController', ['$http', '$scope', function ($http, $scope) {

        var blog = this;
        blog.title = "Really really cool blog service app";

        blog.posts = {};
        $http.get('example.json').success(function (data) {
            blog.posts = data;
        });

        blog.tab = 'blog';


        blog.selectTab = function (setTab) {
            blog.tab = setTab;
            console.log(blog.tab)
        };

        blog.isSelected = function (checkTab) {
            return blog.tab === checkTab;
        };

        blog.post = {};
        blog.addPost = function () {
            blog.post.createdOn = Date.now();
            blog.post.comments = [];
            blog.post.likes = 0;
            blog.posts.unshift(this.post);
            blog.tab = 0;
            blog.post = {};
        };

        blog.addNewUser = function(){
            alert("create user with: " + $scope.reg_user + " " + $scope.reg_fName + " " + $scope.reg_lName + " " + $scope.reg_password);
        };

    }]);

    app.controller('CommentController', function () {
        this.comment = {};
        this.addComment = function (post) {
            this.comment.createdOn = Date.now();
            post.comments.push(this.comment);
            this.comment = {};
        };
    });

})();