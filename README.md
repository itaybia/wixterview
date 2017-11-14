wixterview
======================================================

Wix interview home project.

i wrote the code in MVC way:<br />
model - ProductListRetriever - Holds products and retrieves from the server new product pages when requested.<br />
controller - ProductsListActivity - the main activity that holds the products list and filter box.<br />
view - the different views and the ProductsListAdapter.

the ProductsListActivity receives events from the views/adapter or from the ProductListRetriever when a change in the internal DB happened.
when it receives an event, it notifies the adapter and loads the new data.

the ProductListRetriever is implemented as a singleton.
observers are set to the ProductListRetriever, ProductsListAdapter, to be notified on different events such as a page was loaded, last row in list reached etc.

explanation by steps in the general guidelines:
1) used Volley for the asynchronous HTTP request. since its callbacks are on the UI thread, it made it easy to use to update the UI when a new page arrives.
2) used Glide for the image loading. it is used both in the adapter, and in the fullscreen activity (the detailed view). since Glide cache the images, it made it easier to
    get the image in the detailed view without another image request.
3) the ProductsListAdapter notices when it reaches the last element in the ProductListRetriever's product list, and notifies the observer (the activity) to ask ProductListRetriever
    to retrieve the next page. the activity shows the progress bar until a response arrives.

    ASSUMPTION: the products list is not huge and would not cause a memory strain to hold it. otherwise some changes would need to be made to dispose of pages when reaching a theshold.

4) the ProductListRetriever on new page response checks if a product is already in the product list. the Product object has an "equals" override to accomodate this.
5) the ProductListRetriever has 2 lists. one that holds all products, and one that holds the filtered products. the ProductsListAdapter only shows the filtered list.
    the search EditText view has a textChangeListener that updates the ProductListRetriever with the current filter. ProductListRetriever then filters the list
    and notifies the observer (activity) about the new filtered list. the activity in turn notifies the adapter.

    the ProductsRecyclerView measures the size of a row, and calculates how many can fit in it. if on a page retrieval response, the activity notices that the
    filtered list holds less than the number calculated above, it asks for the next page.
6) Glide mostly takes care of this.
7) When a row is clicked, it calls the listener (activity) with the views in that row. i then use makeSceneTransitionAnimation into another activity in order to get
    the animation of the thumbnail getting larger into the ImageView in the second activity.
