Vue.component("restorani", {
		data: function() {
		return {
			restaurants: null,
			filterRestaurants: null,
			
		}
	},
	
	template:
		`
			<div>
	<div class="topnav" style= "height: 57px">
  	 <router-link tag="a"  to = "/administrator"> Pregled</router-link >
 	 <router-link tag="a" active-class="active" to="/restorani" > Restorani</router-link >
  	 <router-link tag="a" to="/users"> Korisnici</router-link >
	 <router-link tag="a" to="/comments"> Komentari </router-link >
	<div style = "float: right;  padding: 14px 16px" >
		<div class="btn-group">
  	<button type="button" class="btn btn-secondary dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
    		Podesavanja
 		 </button>
  			<div class="dropdown-menu dropdown-menu-right" style="background: #04AA6D">
    			<router-link  class="dropdown-item" style ="font-size: 15px;
    padding: 5px 2px;" to="/adminprofil">Uredi profil</router-link>
    			<router-link  class="dropdown-item" style ="font-size: 15px;
    padding: 5px 2px;" to="/admin-profile">Odjavi se</router-link>
    			
  			</div>
		</div>
 	</div>	
	</div>
		<table class="content-table" >
	
  <thead>
    <tr>
	  <th> Logo </th>
      <th> Naziv</th>
      <th> Tip </th>
      <th> Lokacija  </th>
	  <th> Ocena </th>
	  <th> Obrisi </th>
    </tr>
  </thead>
  <tbody>
   <tr v-for="i in restaurants" >
			<td > <img class ="image5"  v-bind:src="i.logo"> </td>
			<td > {{i.name}}</td>
			<td > {{i.type}} </td>
			<td > {{i.location.adress}} </td>
			<td > {{i.averageGrade}} </td>
			<td class="td"><button type="button" @click = "deleteRestaurant(i)" 
			style = " border-color: #f44336; color: red;   border: 2px solid;" 
			class="btn btn-outline-danger"> Obrisi </button></td>	
		</tr>
  </tbody>
</table>
	<div   style=" margin-left: auto;  margin-right: auto;  width: 1000px;  margin-top: 20px;">        
                <button class= "button5" @click = "goToRestaurantForm" > Dodaj novi restoran </button>
      </div>
	</div>  
	` ,

	
	methods: {
		
		goToRestaurantForm: function(){
			this.$router.push('/addrestaurant');	
		},
		
		deleteRestaurant: function(i){
			axios.delete('rest/restaurants/deleteRestaurant' + i.id)
			i.deleted = true;
			this.filterDelete();
		},
		
		filterDelete: function(){
			this.restaurants = this.restaurants.filter(function(r) {return r.deleted === false});
			this.filterRestaurants = this.users;
		}
		
		
		
	},
	mounted() {
	
		document.body.style.background = "white";
		axios
			.get('rest/restaurants/getAllRest')
			.then(response => (this.restaurants = response.data,			
								this.filterRestaurants = response.data,
								console.log(this.restaurants)));
		
	},
});