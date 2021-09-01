Vue.component("customerComment", {
	data: function () {
		    return {
				restaurant: null,
				comment: null, 
				grade: 1,
				id: null,
				error: false,
				restaurantName: '',
				logo: null,
				adress: null,
		    }
	},
	template:
	`
	<div>
		<div class="topnav">
			<div style="padding-top: 25px; padding-right: 10px; float: right;">
				<button style="color:blue; padding-right:60px" v-on:click="goToBasket()">Prikazi sadrzaj korpe </button>
				<button style="color:blue" v-on:click="logOut()"> Odjavi se! </button>
			</div>
			<div style="padding-top: 20px; padding-left: 10px; float:left;">
				<button v-on:click="homePage()">
					<img src="images/profile.png" style =" width: 45px; height : 45px; padding-bottom:7px;">
				</button>
				<button  v-on:click="redirection()" style="background-color:#5b9966; height:25px; width:65px; padding-bottom:3px;">Profil</button>
			</div>
			<br><br><br>
		</div>
			<br><br><br>
			<div style="background-color: white;" align="center" > 
			<br><h3> Ostavite ocenu i komentar o restoranu </h3>
			<div style="height: 120px; width: 500px; border-width: 3; border-color: black" align="center">  
				<h5>Podaci o restoranu</h5>
				<div>
			    	<img v-bind:src="logo" style="margin-left:2%; float:left; height:60px; width:60px">
			    </div>
				<p align="center">
			 		Restoran: {{restaurantName}} <br> Lokacija restorana: {{adress}} <br> 
			 	</p>
			</div> 
			<form border="1">
				<p>
					<label for="ocena" >Ocenite restoran: </label> 
					<select id="ocena" v-model.number="grade" style="width: 80px; text-align-last:center;">
						<option selected>1</option>
						<option>2</option>
						<option>3</option>
						<option>4</option>
						<option>5</option>
					</select>
				</p>
				<br>
				<p>
					<label for="komentar">Unesite komentar:</label> <br>
					<textarea id="komentar" v-model="comment" style="width: 480px; height: 100px"></textarea>  <!-- required -->
				</p>	
				<p>
					<p v-if="error" style="color: red">Morate izabrati ocenu i uneti komentar</p>
					<button class="button5" v-on:click="sendComment()" style="padding-left: 15px; padding-right: 15px">Potvrdi</button>
				</p>
			</form>
			</div>
		</div>
	</div>
	
	`,
	mounted() {
		this.id = this.$route.params.id;
		axios
	          .get('rest/restaurants/getById' + this.id)
	          .then(response => {this.restaurant = response.data; this.restaurantName = response.data.name; this.logo = response.data.logo; this.adress = response.data.location.adress}); 
	},
	methods: {
		sendComment: function() {
			if(this.comment == null) {
				this.error = true
			} else {
				var comm = {
									"restaurantName": this.restaurantName,  
									"text": this.comment,
									"grade" : this.grade
								};
				axios
		           .post('rest/comments/addComment', comm )
				   .then(alert('Poslali ste komentar'))
          }
		}, 
			   homePage: function() {
			this.$router.push('/homeCustomer');				
		},
		logOut: function(){
			this.$router.push('/')
		},
		redirection: function(){
			this.$router.push("/customerData");
		},
		goToBasket: function(){
			this.$router.push("/basket")
		},
	}
	
}) 