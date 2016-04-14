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

- __Exception Id__: Exceptional circumstances (automatically set but can be changed via the `withExceptionId` method), or the throwing of exceptions isn't actually that exceptional. You'll see endless number of exceptions in your log files. When this happens it can become difficult to find specific exceptions. In order to remedy this I use unique exception Ids for every exception (when using [BusinessException](https://github.com/imamchishty/exception-core/blob/master/src/main/java/com/shedhack/exception/core/BusinessException.java)). The ID can be provided or defaulted to use `UUID`.

- __Correlation Id__: If you're building services (or even microservices) the chances are that you're consuming external services. By the very nature of distributed programming there's a good chance that something will go wrong. If that other service you're consuming also uses this library then it'll have generated an `exception Id`. Well that's great because we now have the ability to trace the error quite easily. That services `exception Id` should be wrapped by your exception (and business codes) as you'll know how to handle it. When that happens you can set your exceptions `correlation Id` to the `exception Id` that was thrown. If you're consuming a service that doesn't use this library then this lib will still attempt to find a correlation Id. If it cannot then this field will be left blank.

- __Params (map)__: Exceptions with just messages are not always enough to give details about the problem, especially contextual. The params property allows developers to add params including values which should help in recreating the failure scenario. Once again this is not mandatory. If you're also using [thread-context-aspect](https://github.com/imamchishty/thread-context-aspect) and mark your methods with [@ThreadContext](https://github.com/imamchishty/thread-context-aspect/blob/master/src/main/java/com/shedhack/thread/context/annotation/ThreadContext.java) then the thread context handler will contain the params. The handler can be injected into your code and be used to access the [thread context](https://github.com/imamchishty/thread-context-handler).

- __Business Codes (list)__: I have already discussed business codes (see above). This property allows several to be added as a list (not mandatory).

- __Http status code__: At the point when an exception is thrown, developers can decide which HTTP status code is most appropriate (not mandatory).

### BusinessException builder (fluent API)
Creating a new Business Exception is very easy, especially when using the builder provided, below is an example:

	new BusinessException.Builder(message, ex).generateId().withBusinessCode(code)
	.withParam("user", "imam").withRequestId("ABCD12335").build();
	
In the above example an existing exception is being wrapped. This isn't necessary and the code below is an example:

	new BusinessException.Builder(message).generateId().withBusinessCode(code)
	.withParam("user", "imam").withRequestId("ABCD12335").build();

## Exception Model

The BusinessException is just a runtime exception, sometimes it is necessary to provide clients with objects which are easier to work with and possibly those with more meta-data. This is why [Exception Model](https://github.com/imamchishty/exception-core/blob/master/src/main/java/com/shedhack/exception/core/ExceptionModel.java) was created. It is used to provide clients with a consistent model in exceptional circumstances. This model is used for ALL exception types and not just BusinessException.

You may have guessed that it contains several of the properties that are provided by BusinessException. Below is a complete list:

- __RequestId__ : unique HTTP request ID, this could be set by the HTTP server (described earlier).

- __ExceptionId__: unique exception ID, can be used to easily find the exception in logs (property set from the exception if it was a BusinessException, otherwise it will be generated).

- __Session Id__: HTTP session Id.

- __ExceptionChainModel (collection)__ : a collection of exception chain models. Useful to see exceptions that have been wrapped up by others. If the exceptions contain exception Id's or correlation Id's then they will be set to the [ExceptionChainModel](https://github.com/imamchishty/exception-core/blob/master/src/main/java/com/shedhack/exception/core/ExceptionChainModel.java). As I mentioned earlier correlation Id are useful so that external service failures can be traced.

- __Http status code__

- __Http status desc__

- __Help Link__: HTTP resource that can detail business codes/http codes.

- __Message__: exception message.

- __Exception Class__: root exception class that was caught and wrapped.

- __Path__: the http resource that was targeted when exception was thrown.

- __Application name__

- __Meta-data__: contains any useful meta-data that was available and added. You could also set this using the thread context handler/aspect mentioned earlier.

- __Business Codes (collection)__: {@link com.shedhack.exception.core.BusinessException}

- __Params__: original params

- __DateTime__: date/time when problem occurred.

## Fluent API

The example below shows how easy it is to build the ExceptionModel:

	BusinessException exception = buildException(FooBusinessCode.FOO_04, buildException(FooBusinessCode.FOO_03,
                buildIllegalArgException(), "Account locked due to too many failed password attempts."), "security");

        ExceptionModel model = new ExceptionModel.Builder("foo", exception)
        	.withHelpLink("http://help")
                .withHttpCode(500, "desc")
                .withPath("/api/v1/resource")
                .withSessionId("abcd1234").build();

The above example builds the model based on a BusinessException type.


## External dependencies

No dependencies used.

## Java requirements

Java 5+.

## Maven central

This artifact is available in [Maven Central](https://maven-badges.herokuapp.com/maven-central/com.shedhack.exception/exception-core).
 
    <dependency>
        <groupId>com.shedhack.exception</groupId>
        <artifactId>exception-core</artifactId>
        <version>See above</version>
    </dependency>    

Contact
-------

	Please feel free to contact me via email, imamchishty@gmail.com




