from django.conf.urls.defaults import *

urlpatterns = patterns('',
     (r'^mysite/', include('mysite.world.urls')),
     (r'^admin/', include('django.contrib.admin.urls')),
)
