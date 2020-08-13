# Liferay JS Generator

The Liferay JS Generator is part of the [Liferay JS Toolkit](https://github.com/liferay/liferay-js-toolkit). It's a pure JavaScript tool with three key functions: 

* [Creating a New JavaScript Application](#create-a-javascript-application)
* [Migrating an Existing React, Angular, or VueJS Application to Liferay DXP](#migrate-existing-applications)
* [Adapting an Existing React, Angular, or VueJS Application for Liferay DXP](#adapt-existing-applications)

This means that the days of needing to know Java to write widgets for Liferay DXP are over.

```note::
Previously the Liferay JS Generator was named The Liferay Bundle Generator. The Liferay Bundle Generator is deprecated as of v2.7.1 of the `Liferay JS Toolkit <https://github.com/liferay/liferay-js-toolkit>`_. If you're still running the Liferay Bundle Generator, install the Liferay JS Generator instead at your earliest convenience.
```

## Create JavaScript Applications

The main function of the Liferay JS Generator is to create JavaScript applications for Liferay DXP. This includes features for adding system and instance settings, localization, and more. See [Creating a JavaScript Widget with the Liferay JS Generator](./developer-guide/creating-a-js-widget-with-the-js-generator.md) for more information.

## Migrate Existing Applications

You can take your existing Angular, React, or VueJS application and migrate your code to a Liferay JS Toolkit project. This is a more involved process than adapting your application, but you get access to all the features and benefits of the Liferay JS Toolkit, such as configuration options and localization. See the documentation for each framework for more information:

* [Migrating React Apps to Liferay DXP](../../../developing-a-single-page-application/using-react.md)
* [Migrating Vue JS Apps to Liferay DXP](../../../developing-a-single-page-application/using-vuejs.md)
* [Migrating Angular Apps to Liferay DXP](../../../developing-a-single-page-application/using-angular.md)

## Adapt Existing Applications

Since v2.15.0 of the Liferay JS Toolkit, you can adapt your JavaScript app's code to run on Liferay DXP. Adapting the code lets you create your app with your favorite workflow ([create-react-app](https://facebook.github.io/create-react-app/), [Angular CLI](https://cli.angular.io/) (any project containing `@angular/cli` as a dependency or devDependency), and [Vue CLI](https://cli.vuejs.org/) (any project containing `@vue/cli-service` as a dependency or devDependency)) and then deploy it without any tweaking. See [Adapting Apps for Liferay DXP](./adapting-apps-for-liferay.md) for more information.

Adapting has its [limitations](https://github.com/liferay/liferay-js-toolkit/wiki/Limitations-of-portlet-adaptation) and has fewer features than a full migration. Some of Liferay DXP integrations may not be available because the native frameworks expect certain things. For example, Angular assumes that it controls a whole Single Page Application as opposed to the small portion of the page that it controls in a Liferay DXP widget. Since webpack bundles all JavaScript in a single file to consume per app, if there are five widgets on a page, you have five copies of the framework in the JavaScript interpreter. To prevent this, adapt your project only if you intend it to be platform-agnostic. Otherwise, [migrate your project](#migrate-existing-applications) to a true Liferay JS Toolkit project.

```note::
Adapted Angular apps don't render after a navigation with SPA active (see `issue #498 <https://github.com/liferay/liferay-js-toolkit/issues/498>`_ for more information). Known limitations are documented in `the wiki <https://github.com/liferay/liferay-js-toolkit/wiki/How-to-adapt-most-popular-frameworks-projects#limitations>`_.
```