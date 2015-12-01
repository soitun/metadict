/// <reference path="../../typings/tsd.d.ts" />

/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Jakob Hendeß
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

"use strict";

import IThemingProvider = angular.material.IThemingProvider;
import IRouteProvider = angular.route.IRouteProvider;

angular.module("MetadictApp", [
    "ngAnimate",
    "ngCookies",
    "ngRoute",
    "ngTouch",
    "ngMaterial",
    "restangular"
]).config(($mdThemingProvider: IThemingProvider) => {
    $mdThemingProvider.theme("default")
        .primaryPalette("blue", {
            "default": "500"
        })
        .accentPalette("red");
}).config(($routeProvider: IRouteProvider) => {
    $routeProvider.when("/search", {
        controller: "SearchController",
        templateUrl: "/views/search.html"
    }).when("/trainer", {
        templateUrl: "/views/trainer.html"
    }).when("/favorites", {
        templateUrl: "/views/favorites.html"
    }).when("/about", {
        templateUrl: "/views/about.html"
    }).when("/help", {
        templateUrl: "/views/help.html"
    }).otherwise({
        redirectTo: "/search"
    });
});
