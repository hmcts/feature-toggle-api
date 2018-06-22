# FF4J web console usage

Library version used to record this document: **1.7.1**.

API specification: [hmcts.github.io/reform-api-docs](https://hmcts.github.io/reform-api-docs/swagger.html?url=https://hmcts.github.io/reform-api-docs/specs/feature-toggle-api.json)

## Table of contents

- [Features](#features)
  - [Feature groups](#feature-groups)
  - [Feature roles](#feature-roles)
    - [Custom roles](#custom-roles)
  - [Feature strategies](#feature-strategies)
- [Bulk import](#bulk-import)
- [Properties](#properties)
- [Appendix A. Sample import file](#appendix-a.-sample-xml-import-file)

## Features

Each feature toggle may be considered as unique map entry of `key -> value` where `key` represents unique identifier aka name of the feature and `value` - true/false.
Since REST API is present, consider feature names formed from alphanumeric characters separated by hyphens/underscores if necessary.

This being said, feature toggles have more features (!):

- groups
- roles
- strategies

The toggle evaluation is _first false - first served_ principle:

- Is feature enabled at all?
- Is feature allowed to view depending on list of roles assigned?
- Is strategy applicable

### Feature groups

Any number of features can be grouped together and toggled at once via menu item `Toggle group`.
Usage example: shut down entire branch of related products or "release went wrong" situation but all is saved with feature toggle being off

### Feature roles

Another built-in nice to have feature is Spring authorities.
At the moment it is poorly implemented and lack of proper management if there is any management at all.
Spring security is configured for both: API and web UI.
In case BasicAuth is provided when **GET**ting feature check it'll take into account built-in roles:

- `ROLE_EDITOR` - can modify features/properties via REST API
- `ROLE_ADMIN` - can access web UI

Let us say we assign `ROLE_ADMIN` to feature `A`.
Then try to check feature on/off status by default without any authorisation present: the response will be `false`.
Providing correct `user:password` credentials - access will be granted and toggle returned as `true`.

#### Custom roles

Using built-in roles is not advised.
They are created for limiting access to API and web UI.
Introduce custom role access instead.

Client is required to provide 2 custom headers:

- **X-USER-ID** - any identification (e.g. email) for Spring security's UserDetails and correctness of ff4j monitoring
- **X-USER-PERMISSIONS** - comma separated value of roles/permissions user has

Once and only once those headers are present for `GET /api/ff4j/check/{feature-id}` custom roles will be picked for challenge instead.

**Note.** Roles are case-sensitive.

### Feature strategies

There are few strategies available from selection but full list (and structure of `initParams`) is available [here](https://github.com/ff4j/ff4j/tree/master/ff4j-core/src/main/java/org/ff4j/strategy).
Strategies are there to override default toggle purpose (on/off value).

As can be inspected in the resource provided earlier, there are few choices to suffice most cases.
Even the *coin flip* strategy called `PonderationStrategy`.
More examples below.

**NOTE**: there is a bug in library not being able to parse `&` in the `ExpressionFlipStrategy`. Not to worry - everything can be replace with `!` and `|`:

```text
A & B = !(!A | !B)
```

So if you want to become adventurous and have XOR in place:

```text
A ^ B
      = (A | B) & !(A & B)
      = (A | B) & (!A | !B)
      = !(!(A | B) | !(!A | !B))
```

## Bulk import

UI provides ability to import features in bulk.
Keep in mind it will override any existing features with the ones in XML file (example below).

## Properties

There are two types of properties: ff4j built-in `PropertyStore` items and custom properties linked to features themselves.
Unfortunately there is no link between built-in one and feature - they are served with independent `Store`.

There are some built-in properties ready to use: string, integer, boolean, date.
Full list can be found [here](https://github.com/ff4j/ff4j/tree/master/ff4j-core/src/main/java/org/ff4j/property).
It is possible to assign custom property: in web form just select `other`.
It has to be within this project.
At the moment this repository does not provide any custom HMCTS properties so in case there is no pre-assigned options available from selection - check the availability in the list mentioned before. 

**NOTE**: in case property will be assigned with some `FixedValues` be aware that it will break and stop showing on UI if current value is not present in the comma separated fixed values.

## APPENDIX A. Sample xml import file

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<features xmlns="http://www.ff4j.org/schema/ff4j"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://www.ff4j.org/schema/ff4j http://ff4j.org/schema/ff4j-1.6.xsd">

    <feature-group name="batch-toggle">
        <feature uid="batch-toggle-1" enable="false" /> 
        <feature uid="batch-toggle-2" enable="false" />
    </feature-group>

    <feature uid="future-release" enable="true" description="Some future release I want to apply in year 2020">
        <flipstrategy class="org.ff4j.strategy.time.ReleaseDateFlipStrategy">
            <param name="releaseDate" value="2020-01-01-01:00" />
        </flipstrategy>
    </feature>

    <feature uid="is-it-home-time-yet" enable="true" description="Smart detector explaining whether one should go home or not">
        <flipstrategy class="org.ff4j.strategy.time.OfficeHourStrategy">
            <param name="monday">08:00-12:00,13:00-17:00</param>
            <param name="tuesday">08:00-12:00,13:00-17:00</param>
            <param name="wednesday">08:00-12:00,13:00-17:00</param>
            <param name="thursday">08:00-12:00,13:00-17:00</param>
            <param name="friday">08:00-12:00,13:00-17:00</param>
        </flipstrategy>
    </feature>

    <feature uid="A" enable="true" />


    <feature uid="B" enable="false" />
    <feature uid="C" enable="true">
        <flipstrategy class="org.ff4j.strategy.el.ExpressionFlipStrategy">
            <param name="expression">!(!A|!B)</param>
        </flipstrategy>
    </feature>

    <feature uid="am-i-success" description="Answer to user whether he is bound to be successful or not" enable="true">
        <security>
            <role name="ROLE_BETA" />
            <role name="ROLE_EDITOR" />
        </security>
    </feature>
</features>
```
