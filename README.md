# Exception Core

[![Build Status](https://travis-ci.org/imamchishty/exception-core.svg?branch=master "Travis CI")](https://travis-ci.org/imamchishty/exception-core) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.shedhack.exception/exception-core/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/com.shedhack.exception/exception-core) 

## Introduction

This library helps in managing Java Runtime Exceptions. It's primary focus is to really make it simple to create and manage exceptions. 

## Business Codes

Rather than creating mutliple exception types I personally prefer to use codes (with descriptions) similar to the way HTTP status codes work. The [Business Codes](https://github.com/imamchishty/exception-core/blob/master/src/main/java/com/shedhack/exception/core/BusinessCode.java) interface contains just two methods, `getCode()` and `getDescription()`. An enum is well placed to implement this interface to provide a list of common business codes for applications. If you're using a tool such as Maven I would suggest the creation of an exceptions module which contains just business codes so that they can easily be shared. Here is an example:

	public enum FooBusinessCode implements BusinessCode {
	
	    FOO_01("User not found."),
	    FOO_02("Users account has been locked."),
	    FOO_03("Users account not active."),
	    FOO_04("Security concern over users account.");
	
	    private final String description;
	
	    FooBusinessCode(String desc) {
	        this.description = desc;
	    }
	
	    public String getCode() {
	        return this.name();
	    }
	
	    public String getDescription() {
	        return description;
	    }
	}

The above business codes can be passed to exceptions so that clients get a good picture as towards the nature of the failure. 

## Business Exception

I briefly mentioned that I prefer business codes with a simple exception type. The exception type that I was referring to was [Business Exception] (https://github.com/imamchishty/exception-core/blob/master/src/main/java/com/shedhack/exception/core/BusinessException.java). This is a generic runtime exception that contains a static builder for easy creation. Before I talk about the builder I'd like to describe this exceptions properties:

- __Request Id__: this library was designed for building restful services. Generally each user has a session Id, although this is good, I prefer to have a unique identifier for each request. This lends itself to easy auditing and monitoring. So if we have a unqiue request Id and that request is stored for auditing/logging purposes we can easily use this ID as a foreign key. We can then tie the exception with the specific HTTP request to gain further contextual knowledge. This is not a mandatory property, although I believe it would make it easier to see the 'bigger picture' and end-to-end visibility of requests. Please refer to [filter-request-id] (https://github.com/imamchishty/filter-request-id) which was created to generate a unique ID for each request (creates a HTTP header).
A final thing I'd add is that if you're using Spring then [thread-context-aspect](https://github.com/imamchishty/thread-context-aspect) and [thread-context-handler](https://github.com/imamchishty/thread-context-handler) would be also good to look at as they'd help in creating the contextual data I spoke of earlier.

- __Exception Id__: Exceptional circumstances, or the throwing of exceptions isn't actually that exceptional. You'll see endless number of exceptions in your log files. When this happens it can become difficult to find specific exceptions. In order to remedy this I use unique exception Ids for every exception (when using [BusinessException](https://github.com/imamchishty/exception-core/blob/master/src/main/java/com/shedhack/exception/core/BusinessException.java)). The ID can be provided or defaulted to use `UUID`.

- __Correlation Id__: If you're building services (or even microservices) the chances are that you're consuming external services. By the very nature of distributed programming there's a good chance that something will go wrong. If that other service you're consuming also uses this library then it'll have generated an `exception Id`. Well that's great because we now have the ability to trace the error quite easily. That services `exception Id` should be wrapped by your exception (and business codes) as you'll know how to handle it. When that happens you can set your exceptions `correlation Id` to the `exception Id` that was thrown. If you're consuming a service that doesn't use this library then this lib will still attempt to find a correlation Id. If it cannot then this field will be left blank.

- __Params (map)__:

- __Business Codes (list)__:

- __Http status code__:

## Exception Model



## Fluent API


## External dependencies


## Java requirements


## Maven central

This artifact is available in Maven Central (link provide at the top of the page).
 
    <dependency>
        <groupId>com.shedhack.exception</groupId>
        <artifactId>exception-core</artifactId>
        <version>See above</version>
    </dependency>    


Contact
-------

	Please feel free to contact me via email, imamchishty@gmail.com




