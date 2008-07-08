from django.conf.urls.defaults import *

urlpatterns = patterns('mysite.world.views',
     (r'^world/$', 'index'),
     (r'^world/(?P<city_id>\d+)/$', 'city'),
)
