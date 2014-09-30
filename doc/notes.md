# Notes

## Architectural Notes.

I'm trying to use Stuart Sierra's
[component](https://github.com/stuartsierra/component) strategy to build
the outermost ring of the Bob Martin
"[Clean Architecture](http://blog.8thlight.com/uncle-bob/2012/08/13/the-clean-architecture.html)".

For a while, I played around with having the browser for
[clj-webdriver](https://github.com/semperos/clj-webdriver) be an
additional component mixed into an application system created fresh for
each test.

I've abandoned this tack for a couple of reasons:

 * I like being able to create an entire system for each test case.
   Starting and stopping a browser for each case is a pain.
   
 * Threading the browser through for every clj-webdriver call is kind of
   a pain. I wrote a macro to make it less annoying, but it's still
   annoying.
   
 * clj-webdriver supports a thread-global driver through set-driver!
   This lets the application system and the browser session start and
   stop separately from each other, which is what I want.  

For now, I think we can live with the thread-global state.  If it causes
any problem, we can take manual control.
   
I *do* like the aproach of
"[PageObject](http://martinfowler.com/bliki/PageObject.html)" style
modules to abstract away the low-level clj-webdriver access.


