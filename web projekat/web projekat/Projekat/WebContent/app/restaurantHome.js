Vue.component("restaurantHome", {
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
			<div style="padding-top: 25px; padding-right: 10px; float: right;">
				<button style="color:blue; padding-right:60px" v-on:click="goToBasket()">Prikazi sadrzaj korpe </button>
				<button style="color:blue" v-on:click="logOut"> Odjavi se! </button>
			</div>
			<div style="padding-top: 20px; padding-left: 10px; float:left;">
				<button v-on:click="homePage()">
					<img src="images/profile.png" style =" width: 45px; height : 45px; padding-bottom:7px;">
				</button>
				<button  v-on:click="redirection()" style="background-color:#5b9966; height:25px; width:65px; padding-bottom:3px;">Profil</button>
				<button style="color:blue; padding-left:75px" v-on:click="goToOrderHistory()">Prikazi istoriju porudzbina </button>
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
				<th> Kolicina </th>
				<th> </th> 
   			</tr>
  		</thead>
  			<tbody>
   				<tr v-for="i in articles" >
					<td > <img class ="image5"  v-bind:src="i.image"> </td>
					<td > {{i.name}}</td>
					<td v-if="i.type == 'FOOD'"> HRANA </td>
					<td v-if="i.type == 'DRINK'"> PIĆE </td>
					<td > {{i.price}} </td>
					<td > <select v-model="i.numberOfArticle">
							<option selected>1</option>
							<option>2</option>
							<option>3</option>
							<option>4</option>
							<option>5</option>
						</select></td>
					<td> <button v-on:click="addArt(i.id)" style="background-color:#d1d7d2; color:blue; width:60px"> Dodaj </button></td>
				</tr>
  			</tbody>
			</table>
		</div>
	</div>
	`,
	methods: {
		addArt : function(id) {
			for(let a of this.articles)
			{
				if(a.id==id)
				{
					
					let ar = {
						"id" : a.id,
						"name" : a.name,
						"type" : a.type,
						"description" : a.description,
						"price" : a.price,
						"quantity" : a.quantity,
						"image" : a.image,
						"numberOfArticle" : a.numberOfArticle,
						"restaurantId" : this.id
					};
					axios
				.post('rest/baskets/addArticle', ar)
				.then(alert('Dodat je novi artikal u korpu'))
				}
			}
		},
		redirection : function(){
			this.$router.push("/customerData");
		},
		goToBasket : function(){
			this.$router.push("/basket")
		},
		goToOrderHistory: function(){
			this.$router.push("orderHistoryCustomer");
		},
		logOut: function() {
			axios
				.get('rest/users/logOutUser')
				.then(alert('Odjavljen korisnik'))
			this.$router.push("/");
		},
		homePage: function(){
			this.$router.push("/homeCustomer")
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