# Update

This is not a bug but actual the way non-reified parameters work: they only ensure their bounds on compile time, but on runtime the casts are unchecked and thus can be whatever they want.

See here: https://youtrack.jetbrains.com/issue/KT-59130/Passing-generic-parameter-to-non-reified-generic-parameter-uses-upper-bound-instead-of-actual-passed-type

# Showcase for a potential bug when passing generic parameters

A small setup that showcases a somewhat unexpected behavior when passing generic parameters repeatedly.

## Premise

The setup is as follows:

A toplevel function calls a function to get a value as a specified type, defined via a generic parameter.
This generic parameter is then used to call another generic function, where the retrieval of the value is simulated
and said value is cast to the type of the generic parameter. If the value is not of a type castable to the initial generic
parameter, this should fail as a ClassCastException.

The used classes are a very simple setup of a sealed class (`Base`) with two implementing data classes (`First`
and `Second`). Note that the base class can also be abstract and open, it doesn't change the observed behavior.

## Testcases

Three cases are tested:

1. Delegating a reified `T` to a reified `T`, i.e.
  ```
  topLevelFunction(): Base  
    -> inline fun <reified T: Base> delegateReifiedToReified(): T  
      -> inline fun <reified T: Any> castToReifiedType(): T  
  ```

2. Delegating a reified `T` to a non-reified `T`, i.e.
```
topLevelFunction(): Base  
  -> inline fun <reified T: Base> delegateReifiedToNonReified(): T  
    -> inline fun <T: Any> castToNonReifiedType(): T  
```

3. Delegating a non-reified `T` to a non-reified `T`, i.e.
```
topLevelFunction(): Base  
  -> inline fun <T: Base> delegateNonReifiedToNonReified(): T  
    -> inline fun <T: Any> castToNonReifiedType(): T  
```

In all three cases, the parameter `T` is passed to the next generic function and thus should stay the
initial value along the call chain. For simplicity's sake, this value is hardcoded to be `First` in the
toplevel call.

## Expected behavior  

If the simulated return value is an instance of `First`, the returned value should simply be returned as such.

If the simulated return value is an instance of `Second`, the cast to the generic `T` in the final should fail
with a ClassCastException as `T` should be `First` via delegation and `Second` is not castable to `First`.

## Actual behavior

If the simulated return value is an instance of `First`, the behavior is expected in all three cases.

If the simulated return value is an instance of `Second`, the behavior differs depending on the test case:
1. delegating reified to reified : works as expected
2. delegating reified to non-reified: does *not* fail on the type cast, returns the `Second` instance instead
3. delegating non-reified to non-reified: does *not* fail on the type cast, returns the `Second` instance instead

Looking into it, it appears that the `T` in the `castToNonReifiedType` function actually becomes its upper
bound `Any`, which is why the cast succeeds.
