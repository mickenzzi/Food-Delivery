Vue.component("guestRestaurantHome", {
	data: function() {
		return {
			restaurantName: '',
			id: '',
			restaurant: {},
			articles: null,
			numberOfArticle : 1,
		}
	},
	template:
		`
	<div>
		<div class="topnav">
			<div style="padding-top: 25px; padding-right: 10px; float: left;">
				<button style="color:blue" v-on:click="homePage"> Idi na pocetnu! </button>
			</div>
			<br><br><br>
			<div style="background-color: white">
				<h2 style="padding-left: 40px; padding-top: 5px">Dobro došli u restoran {{restaurantName}}!</h2>
			</div>
		</div>
		<br><br><br>
		<div  style = " margin-left: auto;  margin-right: auto;  width: 800px;  margin-top: 20px;">
		<h2 style ="text-align:center"> Dostupni artikli </h2><br>
		<table class="content-table" >
    	<thead>
    		<tr>
	  			<th></th>
      			<th> Naziv</th>
      			<th> Tip </th>
				<th> Cena(RSD) </th>
   			</tr>
  		</thead>
  			<tbody>
   				<tr v-for="i in articles" >
					<td > <img class ="image5"  v-bind:src="i.image"> </td>
					<td > {{i.name}}</td>
					<td v-if="i.type == 'FOOD'"> HRANA </td>
					<td v-if="i.type == 'DRINK'"> PIĆE </td>
					<td > {{i.price}} </td>
				</tr>
  			</tbody>
			</table>
		</div>
	</div>
	`,
	methods: {
		homePage: function(){
			this.$router.push("/guestHome")
		}, 
	},
	mounted() {
		alert()
		this.id = this.$route.params.id;
		axios
			.get('rest/restaurants/getById' + this.id)
        	.then(response => {this.restaurant = response.data; this.restaurantName = response.data.name});
		
		axios
			.get('rest/restaurants/getArticlesByRestaurantId' + this.id)
			.then(response => (this.articles = response.data))
	},
});