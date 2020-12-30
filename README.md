# obfuscation-spring

Provides Spring and Spring Boot obfuscation support.

Add [obfuscation-spring-boot-starter](https://robtimus.github.io/obfuscation-spring/obfuscation-spring-boot-starter/dependency-info.html) as a dependency to your project to get the following:

* Autowire support for [Obfuscator](https://robtimus.github.io/obfuscation-core/apidocs/com/github/robtimus/obfuscation/Obfuscator.html).  
    Instances of `Obfuscator` can be autowired in two ways:
    * Annotate an autowired field, constructor argument or method argument with any of the obfuscator annotations of [obfuscation-annotations](https://robtimus.github.io/obfuscation-annotations), and an instance of the matching obfuscator will be autowired.
    * With no obfuscator annotation present, a default obfuscator is used.
* Autowire support for [Obfuscated](https://robtimus.github.io/obfuscation-core/apidocs/com/github/robtimus/obfuscation/Obfuscated.html).  
    Declaring an autowired field, constructor argument or method argument as `Obfuscated` will automatically wrap the autowired field or argument. This supports both autowired values and beans.  
    The obfuscator to use is determined in one of two ways:
    * Annotate the field or argument with any of the obfuscator annotations of [obfuscation-annotations](https://robtimus.github.io/obfuscation-annotations) to use the matching obfuscation rules.
    * With no obfuscator annotation present, a default obfuscator is used.

  The character representation can be specified using [@RepresentedBy](https://robtimus.github.io/obfuscation-annotations/apidocs/com/github/robtimus/obfuscation/annotation/RepresentedBy.html). If this annotation is not present, the [default character representation](https://robtimus.github.io/obfuscation-annotations/apidocs/com/github/robtimus/obfuscation/annotation/CharacterRepresentationProvider.html#getDefaultInstance-java.lang.Class-) is used.

Examples:

    @Autowired
    @ObfuscateFixedLength(8)
    private Obfuscator obfuscator;

    @Autowired
    private Obfuscator defaultObfuscator;

    @Value("${property}")
    @ObfuscateFixedLength(8)
    private Obfuscated<String> obfuscatedValue;

    @Autowired
    @ObfuscateFixedLength(8)
    @RepresentedBy(MyCharacterRepresentation.class)
    private Obfuscated<MyBean> obfuscatedBean;

## Default obfuscator

Out-of-the-box, the default obfuscator is [Obfuscator.fixedLength(3)](https://robtimus.github.io/obfuscation-core/apidocs/com/github/robtimus/obfuscation/Obfuscator.html#fixedLength-int-). This can be overridden in two ways:

* Provide a custom bean of type [Obfuscator](https://robtimus.github.io/obfuscation-core/apidocs/com/github/robtimus/obfuscation/Obfuscator.html).
* Define the default obfuscator in the application properties. See [Properties](https://robtimus.github.io/obfuscation-spring/properties.html) for more information.

## ObfuscatorProvider implementations

If an [ObfuscatorProvider](https://robtimus.github.io/obfuscation-annotations/apidocs/com/github/robtimus/obfuscation/annotation/ObfuscatorProvider.html) type is already available as a bean, this bean will be used. Otherwise, the type is instantiated using Spring's own bean factory. This allows implementations to use autowired fields.

## Vanilla Spring

The automatic support for autowiring `Obfuscator` and `Obfuscated` only works when using `obfuscation-spring-boot-starter`. To add obfuscation support to vanilla Spring:

* Add [obfuscation-spring-beans](https://robtimus.github.io/obfuscation-spring/obfuscation-spring-beans/dependency-info.html) as a dependency to your project instead of `obfuscation-spring-boot-starter`.
* Provide a bean of type [ObfuscatorSupportBeanFactoryPostProcessor](https://robtimus.github.io/obfuscation-spring/apidocs/com/github/robtimus/obfuscation/spring/ObfuscatorSupportBeanFactoryPostProcessor.html) to allow `Obfuscator` to be autowired as above.
* Provide a bean of type [ObfuscatedSupportBeanFactoryPostProcessor](https://robtimus.github.io/obfuscation-spring/apidocs/com/github/robtimus/obfuscation/spring/ObfuscatedSupportBeanFactoryPostProcessor.html) to allow `Obfuscated` to be autowired as above.
* Optionally provide a custom bean of type [Obfuscator](https://robtimus.github.io/obfuscation-core/apidocs/com/github/robtimus/obfuscation/Obfuscator.html) to override the default `Obfuscator`.
