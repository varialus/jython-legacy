from django.shortcuts import render_to_response
from django.http import Http404
from mysite.world.models import City

def index(request):
    five_cities = City.objects.all()[:5]
    data = {'five_cities':five_cities,
           }
    return render_to_response('world/index.html', data)

def city(request, city_id):
    try:
        data = {'city':City.objects.get(pk=city_id)}
    except City.DoesNotExist:
        raise Http404
    return render_to_response('world/city.html', data)
