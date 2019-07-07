# BlurImage

Library to blur the visible activity.

## Installation

build.gradle:

```gradle
    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

app/build.gradle:

```gradle
	dependencies {
	        implementation 'com.github.marcandreappel:blurimage:1.0.0'
	}
```

```gradle
	defaultConfig {
		...
		
		renderscriptTargetApi 21
        	renderscriptSupportModeEnabled true
	}
```

## Usage

As drawable:

```kotlin
    view.background = BitmapDrawable(resources, BlurImage.with(activity).load(snapshot!!).radius(18F).darken().imageBlur)
```

Inserting into ImageView:

```kotlin
    BlurImage.with(activity).load(snapshot!!).radius(18F).darken().into(R.id.imageView)
```
