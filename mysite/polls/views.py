from django.shortcuts import render
from django.http import HttpResponse


def index(request):
    return HttpResponse("HELOO YOU ARE AT POLL")
# Create your views here.
