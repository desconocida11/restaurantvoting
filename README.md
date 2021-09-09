[![Build Status](https://app.travis-ci.com/desconocida11/restaurantvoting.svg?branch=master)](https://app.travis-ci.com/desconocida11/restaurantvoting)
----
Design and implement a REST API using Hibernate/Spring/SpringMVC (or Spring-Boot) **without frontend**.

The task is:

Build a voting system for deciding where to have lunch.

* 2 types of users: admin and regular users
* Admin can input a restaurant, and it's lunch menu of the day (2-5 items usually, just a dish name and price)
* Menu changes each day (admins do the updates)
* Users can vote on which restaurant they want to have lunch at
* Only one vote counted per user
* If user votes again the same day:
    - If it is before 11:00 we assume that he changed his mind.
    - If it is after 11:00 then it is too late, vote can't be changed

Each restaurant provides a new menu each day.

As a result, provide a link to GitHub repository. It should contain the code, README.md with API documentation and couple curl commands to test it.

-----------------------------
P.S.: Make sure everything works with the latest version that is on GitHub :)

P.P.S.: Assume that your API will be used by a frontend developer to build frontend on top of that.

-----------------------------

**Documentation** 
http://localhost:8080/swagger-ui.html

Default users (id, name, email, password, roles):

1, User, user@yandex.ru, password, [USER]

2, Admin, admin@gmail.com, admin, [USER, ADMIN]

3, AdminOnly, admin_only@gmail.com, admin, [ADMIN]

* User with only ADMIN role can't vote for restaurants.
* Information about restaurants, menus and voting results is available for both authenticated and anonymous users (i.e GET requests in /restaurants).
* User can create vote after deadline, but change/delete are prohibited.
* If a restaurant didn't provide menu for today, users can't vote for this restaurant.

### Restaurant Controller

#### GET all restaurants without menus
`curl -X 'GET' 'http://localhost:8080/restaurants' -H 'accept: application/json'`

#### GET all restaurants with menus for today
`curl -X 'GET' 'http://localhost:8080/restaurants/menus' -H 'accept: application/json'`

#### GET all restaurants with menus for particular day
`curl -X 'GET' 'http://localhost:8080/restaurants/menus?date=2021-08-20' -H 'accept: application/json'`

#### GET restaurant by id without menu
`curl -X 'GET' 'http://localhost:8080/restaurants/5' -H 'accept: application/json'`

#### GET restaurant by id and its menu for today
`curl -X 'GET' 'http://localhost:8080/restaurants/5/menus' -H 'accept: application/json'`

#### GET restaurant by id and its menu for particular day
`curl -X 'GET' 'http://localhost:8080/restaurants/5/menus?date=2021-08-20' -H 'accept: application/json'`

#### GET search restaurant by name
`curl -X 'GET' 'http://localhost:8080/restaurants/search?name=Metropol' -H 'accept: application/json'`

#### GET voting results for today
`curl -X 'GET' 'http://localhost:8080/restaurants/votes' -H 'accept: application/json'`

#### GET voting results for particular day
`curl -X 'GET' 'http://localhost:8080/restaurants/votes?date=2021-08-20' -H 'accept: application/json'`

#### GET voting results for restaurant for particular day
`curl -X 'GET' 'http://localhost:8080/restaurants/5/votes?date=2021-08-20' -H 'accept: application/json'`

### Menu Controller

#### POST menu for today
`curl -X 'POST' 'http://localhost:8080/restaurants/5/menus' --user 'admin_only@gmail.com:admin' --header 'accept: application/json' -H 'Content-Type: application/json' -d '{"dishes": [{"price": 100, "name": "русское чаепитие"}, {"price": 200, "name": "пряники"}]}'
`

#### PUT update menu for today
`curl -X 'PUT' 'http://localhost:8080/restaurants/5/menus' --user 'admin_only@gmail.com:admin' --header 'accept: application/json' -H 'Content-Type: application/json' -d '{"dishes": [{
"id": 11,
"price": 5800,
"name": "Chicken Curry with French Fries"
}]}'
`

#### DELETE menu for today (only if there are no votes)
`curl -X 'DELETE' 'http://localhost:8080/restaurants/5/menus' --user 'admin_only@gmail.com:admin' --header 'accept: application/json'`

#### DELETE particular dish in today's menu (only if there are no votes)
`curl -X 'DELETE' 'http://localhost:8080/restaurants/5/menus?dish=12' --user 'admin_only@gmail.com:admin' --header 'accept: application/json'`

### Vote Controller

#### POST a vote for restaurant
`curl -X 'POST' 'http://localhost:8080/votes?restaurant=4' --user 'user@yandex.ru:password' --header 'accept: application/json'`

#### GET users' votes
`curl -X 'GET' 'http://localhost:8080/votes' --user 'user@yandex.ru:password' --header 'accept: application/json'`

#### GET vote by id 
`curl -X 'GET' 'http://localhost:8080/votes/21' --user 'user@yandex.ru:password' --header 'accept: application/json'`

#### DELETE vote by id
`curl -X 'DELETE' 'http://localhost:8080/votes/21' --user 'user@yandex.ru:password' --header 'accept: application/json'`

#### PUT change the vote
`curl -X 'PUT' 'http://localhost:8080/votes/20?restaurant=4' --user 'admin@gmail.com:admin' --header 'accept: application/json'`


