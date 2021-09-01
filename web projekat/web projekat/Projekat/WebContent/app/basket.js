Vue.component("basket", {
	data: function () {
		    return {
		      basket: null,
		      articles: [],
		      paragraf: false,
			  filterArticles: null,
		      table: false,
			  totalPrice: 0,
			  username: '',
			  empty: true,
			  id: '',
			
		    }
	},
	template: 
	` 
	<div>
		<div class="topnav">
			<div style="padding-top: 25px; padding-right: 10px; float: right;">
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
		</div>
			<div style="background-color: white" align="center"><br>
			<h2> Sadrzaj korpe </h2>
			<div v-if="empty == false">
				<table class="content-table" border="0" >
				<thead bgcolor="lightyellow">
				<th > Slika </th>
				<th > Naziv </th>
				<th > Cena </th>
				<th > Kolicina(kom) </th>
				<th ></th>
				</thead>
				<tr v-for="i in this.articles">
					<td > <img class ="image5" v-bind:src="i.image"></td>
					<td > {{i.name}}</td>
					<td > {{i.price}}</td>
					<td > {{i.numberOfArticle}} </td>
					<td > <button v-on:click="deleteArticle(i)" > ❌ </button> </td>
				</tr>
				</table><br>
				<p>____________________________________</p>
				<p>
					Ukupno:<b>{{this.articles[0].totalPrice}} dinara</b>
				</p>
				<br> 
				<button class="button5" v-on:click="deleteBasket(this.basket)">Isprazni korpu</button>
				<button class="button5" v-on:click="order()">Poruči</button>
			</div>
			<div v-if="empty == true">
				<p> Nema artikala u korpi </p>
			</div>
		</div>
	</div>		  
	`,
	methods: {
		deleteBasket: function() { 
			if (confirm('Da li ste sigurni da zelite da ispraznite korpu?') == true) {
				axios
					.delete('rest/baskets/deleteBasket')
					.then(this.filterDeleteBasket)
			}
		},
		filterDeleteBasket: function(){
			for(article in this.articles){
				article.deleted=true;
			}
			this.articles = this.articles.filter(function(r) {return r.deleted === false});
			this.empty=true;
			
		},
		deleteArticle: function(art) { 
			alert(art.name)
			axios
				.delete('rest/baskets/deleteArticle/' + art.name)
				.then( response => {
					if(this.articles.length==1)
					{
						axios
							.delete('rest/baskets/deleteBasket')
							.then(this.filterDeleteBasket)
					}
					else
					{
						this.getAllArticles();
					}
					});
		},
		order: function() { 
			axios
				.get('rest/baskets/addOrder', this.id)
				.then(alert('Poslali ste porudzbinu'))
		},
		getAllArticles: function() {
			axios
				.get('rest/baskets/getBasket')
				.then(response => { this.articles = response.data; })
			return this.articles
		},
		redirection : function(){
			this.$router.push("/customerData");
		},
		goToOrderHistory: function(){
			this.$router.push("orderHistoryCustomer");
		},
		homePage(){
			this.$router.push("/homeCustomer")
		},
		logOut: function() {
			axios
				.get('rest/users/logOutUser')
				.then(alert('Odjavljen korisnik'))
			this.$router.push("/");
		},
	},
	mounted() {
		axios
			.get('rest/baskets/getBasket')
			.then(response => { this.articles = response.data;
								if(response.data.length != 0) {
									this.empty=false;
								} else {
									this.empty=true;
								}})
	},
})