Vue.component("homeCustomer", {
	data: function() {
		return {
			users: null,
			username: "",
			restaurants: null,
			searchName: '',
			searchLocation: '',
			searchType: '',
			searchGrade: '',
			searchField: '',
			ready: false,
			filterRestaurants: null,
			type: "ALL",
			status: "ALL",
			enableFilter : true,
			enableSort : true,
			readyBegin: false,
			filterRestaurantType: '',
			filterRestaurantStatus: '',
			number : 0, //1-name,2-location,3-avgGrade
			selected1: false,
			selected2: false,
			selectedSort1: false,
			selectedSort2: false,
			selectedSort3: false,
			enableMap: true,
			previewMap: true,

		}
	},
	template:
		`
	<div>
	<div class="topnav">
			<div style="padding-top: 25px; padding-right: 10px; float: right;">
				<button style="color:blue; padding-right:60px" v-on:click="goToBasket()">Prikazi sadrzaj korpe </button>
				<button style="color:blue" v-on:click="logOut"> Odjavi se! </button>
			</div>
		<div style="padding-top: 20px; padding-left: 10px; float:left;">
		<img src="images/profile.png" style =" width: 45px; height : 45px; padding-bottom:7px;">
		<button  v-on:click="redirection()" style="background-color:#5b9966; height:25px; width:65px; padding-bottom:3px;">Profil</button>
		<button style="color:blue; padding-left:75px" v-on:click="goToOrderHistory()">Prikazi istoriju porudzbina </button>
		</div>
		<br><br><br>
		<div style="background-color: white">
			<h2 style="padding-left: 40px; padding-top: 5px">Dobro došli {{username}}!</h2>
		</div>
	</div>
	<div  style = " margin-left: auto;  margin-right: auto;  width: 800px;  margin-top: 20px;">
	<h2 style ="text-align:center"> Dostupni restorani </h2><br>
	<input style="width: 120px;"   type="search" v-model="searchName" placeholder='Naziv' >
	<input style="width: 120px;" type="search"  v-model="searchType" placeholder='Tip' >
	<input style="width: 120px;" type="search"  v-model="searchLocation" placeholder='Lokacija' >
	
	<select v-on:change="onChange(filterRestaurantStatus, filterRestaurantType, searchName, searchLocation, searchType, searchGrade)" v-model="filterRestaurantType"  style = "width: 120px; height: 30px" >
    <option value="">Izaberite tip restorana</option>
    <option value="ITALIAN">Italijanski</option>
    <option value="CHINESE">Kineski</option>
	<option value="MEXICAN">Meksicki</option>
	<option value="INDIAN">Indijski</option>
	<option value="FAST_FOOD">Brza hrana</option>
	<option value="BARBECUE">Rostilj</option>
  </select>
	<select v-on:change="onChange(filterRestaurantStatus, filterRestaurantType, searchName, searchLocation, searchType, searchGrade)" v-model="filterRestaurantStatus" style = "width: 120px; height: 30px">
    <option value="">Otvoreni/Zatvoreni</option>
    <option value="OPEN"> Otvoren </option>
    <option value="CLOSED"> Zatvoren </option>
  </select>
  <select v-on:change="onChange(filterRestaurantStatus, filterRestaurantType, searchName, searchLocation, searchType, searchGrade)" v-model="searchGrade" style = "width: 120px; height: 30px">
    <option value="">Izaberite ocenu</option>
	<option value="1"> 1 </option>
    <option value="2"> 2 </option>
    <option value="3"> 3 </option>
	<option value="4"> 4 </option>
	<option value="5"> 5 </option>
  </select>
	<button type = "submit" v-on:click="searchRestaurant(searchName,searchLocation,searchGrade)" style = "width: 30px; height : 30px; margin-left = 60px border-radius: 5px;   border: 2px solid black "   > 
		<img src="images/search-user.png" style =" width: 30px; height : 30px; margin-left = 60px ">
	</button>
	<br>
	<label>Sortiraj po:</label>
	 <input type="radio" v-model="selectedSort1" id="name" name="sort" value="naziv" v-on:click="sortByName()">
	 <label> Naziv</label>
	 <input type="radio" id="location" v-model="selectedSort2"  name="sort" value="lokacija" v-on:click="sortByLocation()">
	 <label> Lokacija</label>
	 <input type="radio" id="grade" v-model="selectedSort3" name="sort" value="prosecnaOcena" v-on:click="sortByAverageGrade()">
	 <label> Prosecna ocena</label><br>
	<label>Nacin sortiranja:</label>
     <input type="radio" id="rastuce" value="rastuce" v-model="selected1" name="nacinSortiranja" v-on:click="ascendingSort(number, filterRestaurantStatus, filterRestaurantType, searchName, searchLocation, searchType, searchGrade)">
	 <label for="rastuce"> Rastuce</label>
	 <input type="radio" id="opadajuce" value="opadajuce" v-model="selected2" name="nacinSortiranja" v-on:click="descendingSort(number, filterRestaurantStatus, filterRestaurantType, searchName, searchLocation, searchType, searchGrade)">
	 <label for="opadajuce"> Opadajuce</label><br>
	</div>
	
	<table class="content-table" >
    	<thead>
    		<tr>
	  			<th> Logo </th>
      			<th> Naziv</th>
      			<th> Tip </th>
      			<th> Lokacija  </th>
	  			<th> Ocena </th>
	  			<th> Dostupan </th>
   			</tr>
  		</thead>
  	<tbody>
   	<tr v-for="i in restaurants" >
			<td > <img class ="image5"  v-bind:src="i.logo"> </td>
			<td > {{i.name}}</td>
			<td > {{i.type}} </td>
			<td > {{i.location.adress}} </td>
			<td > {{i.averageGrade}} </td>
			<td v-if="i.status == 'OPEN'"> <button style="color:blue" v-on:click="goToRestaurant(i.id)"> Naruci! </button> </td>
			<td v-if="i.status == 'CLOSED'"> ZATVORENO </td>
		</tr>
  	</tbody>
	</table>
	
	<div v-if="enableMap">
			<br>
			<table align="center" style="width:35%" >	
				<tr> <td align="center">
						  <button style="background-color:lightgray;border:1px solid lightgray" type="button" v-on:click="previewMapForSearch()"> Izaberite na mapi </button> 
						  <input style="width: 170px; height: 35px" type="search" id="cityID" v-model="searchField" v-bind:class="{filledInput: searchField != '' }" placeholder="Grad, mesto..." > 
						  <button style="background-color:lightgray;border:1px solid lightgray" type="button" v-on:click="searchOnMap(searchField)"> Pretrazi </button> 
					 </td> 
				</tr>
				<tr> <div id="mapSearch" class="mapSearch" v-if="previewMap"> </div> </tr>
			</table>
		</div>
	</div>
	`,
	methods:
	{
		logOut: function() {
			axios
				.get('rest/users/logOutUser')
				.then(alert('Odjavljen korisnik'))
			this.$router.push("/");
		},
		homePage: function() {
			this.$router.push("/")
		},
		goToRestaurant: function(id) {
				router.push(`/restaurantHome/${id}`)
		},
		goToBasket : function(){
			this.$router.push("/basket");
		},
		goToOrderHistory: function(){
			this.$router.push("orderHistoryCustomer");
		},
		onChange: function(filterStatus, filterType, name, location, type, grade)
		{
			if (!filterType)
			{
				filterType = " ";
			}
				
			if (!filterStatus)
			{
				filterStatus = " ";
			}	
				
			if (!name)
			{
				name = " ";
			}
				
			if (!location)
			{
				location = " ";
			}
				
			if (!type)
			{
				type = " ";
			}
				
			if (!grade)
			{
				grade = "0";
			}	
			axios
	       		.get('rest/restaurants/filter' + '/' + filterStatus + '/' + filterType + '/' + name + '/' + location + '/' + type + '/' + grade)
        		.then(response => {this.restaurants = response.data; this.readyBegin = true})
		},
		searchRestaurant: function (name, location, type, grade) {		
			if(name && location && type && grade)
			{
				axios
	          		.get('rest/restaurants/searchRestaurant' + '/' + name + '/' + location + '/' + type + '/' + grade)
	          		.then(response => {this.restaurants = response.data; this.readyBegin = true})		
			}
			else 
			{	
				if (!name)
				{
					name = " ";
				}
				
				if (!location)
				{
					location = " ";
				}
				
				if (!type)
				{
					type = " ";
				}
				
				if (!grade)
				{
					grade = "0";
				}
				
				axios
	          		.get('rest/restaurants/searchRestaurant' + '/' + name + '/' + location + '/' + type + '/' + grade)
	          		.then(response => {this.restaurants = response.data; this.readyBegin = true})
			}
		},
		
		sortByName: function() {
			this.ready = true;
			this.number = 1;
			this.selected1 = false;
			this.selected2 = false;
			this.selectedSort1=true;
			this.selectedSort2=false;
			this.selectedSort3=false;
		},
		sortByLocation: function() {
			this.ready = true;
			this.number = 2;
			this.selected1 = false;
			this.selected2 = false;
			this.selectedSort1=false;
			this.selectedSort2=true;
			this.selectedSort3=false;			
		},
		sortByAverageGrade: function() {
			this.ready = true;
			this.number = 3;
			this.selected1 = false;
			this.selected2 = false;
			this.selectedSort1=false;
			this.selectedSort2=false;
			this.selectedSort3=true;
		},
		descendingSort: function(number, filterStatus, filterType, name, location, type, grade) {
			this.selected1=false;
			if (!filterType)
			{
				filterType = " ";
			}
				
			if (!filterStatus)
			{
				filterStatus = " ";
			}	
				
			if (!name)
			{
				name = " ";
			}
				
			if (!location)
			{
				location = " ";
			}
				
			if (!type)
			{
				type = " ";
			}
				
			if (!grade)
			{
				grade = "0";
			}		
			
			axios
          	   .get('rest/restaurants/descendingSort'+ '/' + number +  '/' + filterStatus + '/' + filterType + '/' + name + '/' + location + '/' + type + '/' + grade)
          	   .then(response => (this.restaurants = response.data))
		},
		ascendingSort: function(number, filterStatus, filterType, name, location, type, grade) {
			this.selected2=false;
			if (!filterType)
			{
				filterType = " ";
			}
				
			if (!filterStatus)
			{
				filterStatus = " ";
			}	
				
			if (!name)
			{
				name = " ";
			}
				
			if (!location)
			{
				location = " ";
			}
				
			if (!type)
			{
				type = " ";
			}
				
			if (!grade)
			{
				grade = "0";
			}	
		
			axios
          	   .get('rest/restaurants/ascendingSort' + '/' + number +  '/' + filterStatus + '/' + filterType + '/' + name + '/' + location + '/' + type + '/' + grade)
          	   .then(response => (this.restaurants = response.data))
		},
		redirection : function(){
			this.$router.push("/customerData");
		},
		initForMap: function () {

            const mapSearch = new ol.Map({
                target: 'mapSearch',
                layers: [
                    new ol.layer.Tile({
                        source: new ol.source.OSM()
                    })
                ],
                view: new ol.View({
                    center: [0, 0],
                    zoom: 2
                })
            })

            mapSearch.on('click', function (evt) {
                var coord = ol.proj.toLonLat(evt.coordinate);
                reverseGeocode(coord);

            })

        },
		 previewMapForSearch: function () {
            this.previewMap = !this.previewMap;
            if (this.previewMap) {
                // Draw map on screen
                this.$nextTick(function () {
                    this.initForMap();

                    // Seting some extra style for map
                    let c = document.getElementById("mapSearch").childNodes;
                    c[0].style.borderRadius  = '10px';
                    c[0].style.border = '4px solid lightgrey';
                })
            }
        },
		searchOnMap: function (location) { 
			const cyrillicPattern = /[^\u0000-\u007E]/g;
			
			var lat = [
		     "A", "B", "V", "G", "D", "Đ", "E", "Ž", "Z", "I", "J", "K", "L", "Lj", "M", "N", "Nj", "O", "P", "R", "S", "T", "Ć", "U", "F", "H", "C", "Č", "Dž", "Š",
		     "a", "b", "v", "g", "d", "đ", "e", "ž", "z", "i", "j", "k", "l", "lj", "m", "n", "nj", "o", "p", "r", "s", "t", "ć", "u", "f", "h", "c", "č", "dž", "š"]
		
			var cyr = [
		     "А", "Б", "В", "Г", "Д", "Ђ", "Е", "Ж", "З", "И", "Ј", "К", "Л", "Љ", "М", "Н", "Њ", "О", "П", "Р", "С", "Т", "Ћ", "У", "Ф", "Х", "Ц", "Ч", "Џ", "Ш",
		     "а", "б", "в", "г", "д", "ђ", "е", "ж", "з", "и", "ј", "к", "л", "љ", "м", "н", "њ", "о", "п", "р", "с", "т", "ћ", "у", "ф", "х", "ц", "ч", "џ", "ш" ]
						
			if (cyrillicPattern.test(location))
			{	
				for(var i = 0; i < lat.length; i++) 
				{
				    var tt = lat[i];
   					location = location.replace(new RegExp(cyr[i], "g"),tt);
				}
 			}				
								
			axios
          		.get('rest/restaurants/searchMap' + location)
          		.then(response => {this.restaurants = response.data; this.readyBegin = true})		
		},
		enMapa: function(){
			this.enableMapa = true
		}
	},
	mounted() {
		alert()
		this.$nextTick(function () {
            this.initForMap();
        })
		axios
			.get('rest/users/getLoginUser')
			.then(response => { this.username = response.data.username; this.ready = true });
		axios
			.get('rest/restaurants/getAllRest')
			.then(response => (this.restaurants = response.data))

	},
});

function reverseGeocode(coords) {
    fetch('http://nominatim.openstreetmap.org/reverse?format=json&lon=' + coords[0] + '&lat=' + coords[1])
        .then(function (response) {
            return response.json();
        }).then(function (json) {

            // GRAD 
            if (json.address.city) {
                let el = document.getElementById("cityID");
                el.value = json.address.city;
                el.dispatchEvent(new Event('input'));
            } else if (json.address.city_district) {
                let el = document.getElementById("cityID");
                el.value = json.address.city_district;
                el.dispatchEvent(new Event('input'));
            }
        });
}