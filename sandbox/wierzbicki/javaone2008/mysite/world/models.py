from django.db import models

class Continent(models.Model):
    name = models.CharField(max_length=14, default='')

    def __unicode__(self):
        return self.name

    class Admin:
        pass

class Country(models.Model):
    code = models.CharField(max_length=3, default='')
    name = models.CharField(max_length=52, default='')
    continent = models.ForeignKey(Continent)
    region = models.CharField(max_length=26, default='')
    surface_area = models.DecimalField(max_digits=10, decimal_places=2, default='0.00')
    indep_year = models.IntegerField(null=True)
    population = models.IntegerField(default=0)
    life_expectancy = models.DecimalField(max_digits=3, decimal_places=1, null=True)
    gnp = models.DecimalField(max_digits=10, decimal_places=2)
    gnp_old = models.DecimalField(max_digits=10, decimal_places=2, null=True)
    local_name = models.CharField(max_length=45, default='')
    government_form = models.CharField(max_length=45, default='')
    head_of_state = models.CharField(max_length=60, default='')
    capital = models.IntegerField(null=True)
    code2 = models.CharField(max_length=2, default='')

    def __unicode__(self):
        return self.name

    class Admin:
        list_display = ('code',
            'name',
            'continent',
            'region',
            'surface_area',
            'indep_year',
            'population',
            'life_expectancy',
            'gnp',
            'gnp_old',
            'local_name',
            'government_form',
            'head_of_state',
            'capital',
            'code2',
            )

        search_fields = ('code',
            'name',
            )


class City(models.Model):
    name = models.CharField(max_length=35, default='')
    #country_code = models.CharField(max_length=3, default='')
    country = models.ForeignKey(Country)
    district = models.CharField(max_length=20, default='')
    population = models.IntegerField(default=0)

    def __unicode__(self):
        return self.name

    class Admin:
        list_display = ('name',
                'country',
                'district',
                #'population',
                )

        list_filter = ('country',
                )

        search_fields = (
                'name',
                'district',
                )

class CountryLanguage(models.Model):
    #country_code = models.CharField(max_length=3, default='')
    country = models.ForeignKey(Country)
    language = models.CharField(max_length=30, default='')
    is_official = models.BooleanField(default=False)
    percentage = models.DecimalField(max_digits=4, decimal_places=1, default='0.0')

    def __unicode__(self):
        return self.language

    class Admin:
        pass


