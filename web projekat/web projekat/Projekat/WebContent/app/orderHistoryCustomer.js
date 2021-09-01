Vue.component("orderHistoryCustomer", {
	data: function () {
		    return {
  				orders: null,
  				sort: {
			        field: '',
			        desc: true        
			    },
				currentSort:'restaurant.name',
  				currentSortDir:'asc', 
			   	noDelivered: false,
			   	noUndelivered: false,
				filter: '', 
				filterPrice1: '',
				filterPrice2: '',
				filterDate1: '',
				filterDate2: '',
				filterType: 'Tip restorana',
				filterStatus: 'Status porudzbine',
				filterCriterion: 'Filtriraj po...',
				dateVisibility: false,
				priceVisibility: false,
				currentSort: '',
				buttonVisibility: false,
				
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
			<div style="background-color: white" align="center"><br>
			<h2>Pregled porudzbina</h2> 
				<input style="width: 170px;" type="search"  placeholder='Naziv restorana' v-model="filter">
				<select style="width: 180px; height: 30px" type="search" v-model="filterType"><option disabled selected>Tip restorana</option><option>ITALIAN</option><option>CHINESE</option><option>BARBECUE</option><option>INDIAN</option><option>MEXICAN</option><option>FAST_FOOD</option></select>
				<select style="width: 180px; height: 30px" type="search" v-model="filterStatus"><option disabled selected>Status porudzbine</option><option>IN_PROCESSING</option><option>IN_PREPARATION</option><option>ON_HOLD</option><option>SENT</option><option>DELIVERED</option><option>REJECTED</option><option>REQUEST_SENT</option></select> 
				<select style="width: 180px; height: 30px" type="search" v-model="filterCriterion" @change="criterion"><option disabled selected>Filtriraj po...</option><option>Ceni</option><option>Datumu</option></select>
				<button v-if="buttonVisibility" @click="cancelAllFilters">❌</button>
				<div v-if="dateVisibility">
				<vuejs-datepicker v-if="dateVisibility" style="width: 150px; padding-top: 5px; padding-bottom: 5px" type="search" format="dd-MM-yyyy"   placeholder='📆 Datum od ' v-model="filterDate1"></vuejs-datepicker>
				<vuejs-datepicker v-if="dateVisibility" style="width: 150px;" format="dd-MM-yyyy" placeholder='📆 Datum do' type="search" v-model="filterDate2"></vuejs-datepicker>
				<div style="padding-top: 5px; padding-left:20px; height: 10px">
					<button @click="emptyDate2">Ponisti kriterijume pretrage</button>
				</div>
				</div>
				<div v-if="priceVisibility">
				<div style="padding-top: 5px; padding-bottom: 5px">
				<input v-if="priceVisibility" style="width: 150px;" type="search"  placeholder='Cena od' v-model="filterPrice1">
				</div>
				<input v-if="priceVisibility" style="width: 150px;" type="search"  placeholder='Cena do' v-model="filterPrice2">
				</div>
				<br><br>
				<table border="1" id="porudzbine2" style="width: 1000px" v-if="ready">
					<thead bgcolor="lightgrey">
							<th >Datum i vreme porudzbine <img src="images/elevator.ico" style="cursor: pointer;" @click="doSort('dateTimeOrder')"></th>
							<th >Restoran <img src="images/elevator.ico" style="cursor: pointer;" @click="doSort('restaurant.name')"></th>
							<th > Tip restorana </th>
							<th >Status porudzbine</th>
							<th >Ukupna cena  <img src="images/elevator.ico" style="cursor: pointer;" @click="doSort('price')"></th>
							<th >Artikli</th><th></th>
					</thead>
					<tbody>
						<tr v-for="order in sortedData" v-if="rowVisibilityForProcessing(order)">
							<td ><b> {{order.dateTimeOrder}} </b></td>
							<td > {{order.restaurant.name}} </td>
							<td > {{order.restaurant.type}} </td>
							<td > {{order.status}} </td>
							<td > <b>{{order.price}}</b> </td>
							<td ><ul id="example-1">
								<p v-for="(i, br) in order.orderedArticle" :key="i.name">
								  	<b></b> <img v-bind:src="i.image" class="image5" > <br>
								    <b>Naziv:</b> {{ i.name }} <br>
								    <b> Cena: </b> {{i.price}} din <br>
								    <b> Komada: </b> {{ order.numberOfOrder[br] }} <br>
								  </p> 
								</ul>
							</td>
							<td v-if="order.status === 'IN_PROCESSING'">
								<button @click="rejectOrder(order.id)" style="color:red">Otkazi porudzbinu</button>
							</td>
							<td></td>	
						</tr>
					</tbody>
				</table>
				<p v-if="noUndelivered" align="center">Nema porudzbina u stanju obrade</p>
				<br><br>
				<h2>Istorija porudzbina</h2>   
				<table border="1" id="porudzbine" style="width: 1000px">
					<thead bgcolor="lightgrey">
						<th>Datum i vreme porudzbine <img src="images/elevator.ico" style="cursor: pointer;" @click="doSort('dateTimeOrder')"></th>
						<th>Restoran <img src="images/elevator.ico" style="cursor: pointer;" @click="doSort('restaurant.name')"></th>
						<th>Tip restorana</th>
						<th>Status porudzbine</th>
						<th>Ukupna cena  <img src="images/elevator.ico" style="cursor: pointer;" @click="doSort('price')"></th>
						<th>Artikli</th><th></th>
					</thead>
					<tr v-for="order in sortedData" v-if="rowVisibility(order)">
						<td ><b> {{order.dateTimeOrder}} </b></td>
						<td > {{order.restaurant.name}} </td>
						<td > {{order.restaurant.type}} </td>
						<td > {{order.status}} </td>
						<td > <b>{{order.price}}</b> </td>
						<td >
							<ul id="example-1">
							<p v-for="(i, br) in order.orderedArticle" :key="i.name">
							  	<b></b> <img v-bind:src="i.image" class="image5" > <br>
							    <b>Naziv:</b> {{ i.name }} <br>
							    <b> Cena: </b> {{i.price}} din <br>
							    <b> Komada: </b> {{ order.numberOfOrder[br] }} <br>
							  </p> 
							</ul>
						</td>
						<td v-if="order.status === 'DELIVERED'"> 
							<button v-on:click="sendComment(order.restaurant.id)" style="color:blue"> Ostavite svoj utisak </button>
						</td>
					</tr>
				</table>
				<p v-if="noDelivered">Prazna istorija porudzbina</p>
			<br><br><br>
		</div>
	</div>
	`,
	mounted() {
		alert()
		axios
			.get('rest/users/getOrders')
			.then(response => { this.orders = response.data; this.ready = true;
								this.deliveredOrder(); this.orderNotDelivered(); })
	},
	methods: {
		rejectOrder: function(id) {
			axios
				.delete('rest/baskets/deleteOrder' + id)
				.then(alert('Otkazali ste porudzbinu'))
		},
		rowVisibilityForProcessing: function(order) {
			return !order.deleted && order.status != 'DELIVERED'
		},
		rowVisibility: function(order) {
			return !order.deleted && order.status === 'DELIVERED'
		},
		sendComment: function(id){
			router.push(`/customerComment/${id}`)
		},
		deliveredOrder: function() {
			if(this.orders.length == 0){
				return true;
			}
			let statusi = []
			for(let o of this.orders){
				if(o.deleted == false){
					statusi.push(o.status)
				}
			}
			var retVal = statusi.includes("IN_PROCESSING") || statusi.includes("IN_PREPARATION") || statusi.includes("ON_HOLD") 
						|| statusi.includes("SENT") || statusi.includes("REJECTED") || statusi.includes("REQUEST_SENT") ;
			this.noUndelivered = !retVal;
		},
		orderNotDelivered: function() {
			if(this.orders.length == 0){
				return true;
			}
			let statusi = []
			for(let o of this.orders){
				if(o.deleted == false){
					statusi.push(o.status)
				}
			}
			var retVal = statusi.includes("DELIVERED");
			console.log(retVal)
			this.noDelivered = !retVal;
		},
	
  		ready: function() {
	        return true;
		},
		doSort (field) {
			//alert(field)
	      if(field == this.sort.field){
	        this.sort.desc = !this.sort.desc
	      } else{
	        this.sort.field = field;
	        this.sort.desc = true;
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
		formatDate: function (date) {
	    var d = new Date(date),
	        month = '' + (d.getMonth() + 1),
	        day = '' + d.getDate(),
	        year = d.getFullYear();
	
	    if (month.length < 2) 
	        month = '0' + month;
	    if (day.length < 2) 
	        day = '0' + day;
	
	    return [day, month, year].join('-');
		},
		criterion: function(){
			if(this.filterCriterion == 'Ceni'){
				this.buttonVisibility = true;
				this.priceVisibility = true;
				this.dateVisibility = false;
			} else if(this.filterCriterion == "Datumu"){
				this.buttonVisibility = true;
				this.dateVisibility = true;
				this.priceVisibility = false;
			}
		},
		emptyDate2: function(){
			this.filterDate2 = ''
			this.filterDate1 = ''
		},
		cancelAllFilters: function(){
  			this.filter= '', 
			this.filterPrice1= '',
			this.filterPrice2= '',
			this.filterDatem1 = '',
			this.filterDatem2 = '',
			this.filterType = 'Tip restorana',
			this.filterStatus = 'Status porudzbine',
			this.filterCriterion = 'Filtriraj po...',
			this.dateVisibility = false;
			this.priceVisibility = false;
			this.buttonVisibility = false;
  		},
		sorting: function(s) {
		    if(s === this.currentSort) {
		      this.currentSortDir = this.currentSortDir==='asc'?'desc':'asc';
		    }
		    this.currentSort = s;
  		},
	},
	computed:{
		filteredOrders() {
	      return this.orders
			.filter(c => {
		        if(this.filter == '') return true;
				this.buttonVisibility = true;
		        return (c.restaurant.name.toLowerCase().indexOf(this.filter.toLowerCase()) >= 0)})
			.filter(c => {
		        if(this.filterPrice1 == '' || this.filterPrice2 == '') return true;
				this.buttonVisibility = true;
				if((parseInt(this.filterPrice1) < c.price || parseInt(this.filterPrice1) == c.price) 
					&& parseInt(this.filterPrice2) >= c.price) {  return true; } }) 
			.filter(c => {
		        if(this.filterType == 'Tip restorana') return true;
				this.buttonVisibility = true;
		        return (c.restaurant.type.toLowerCase().indexOf(this.filterType.toLowerCase()) >= 0)})
		   .filter(c => {
		        if(this.filterStatus == 'Status porudzbine') return true;
				this.buttonVisibility = true;
				if(this.filterStatus == 'ON_HOLD'){ this.filterStatus = 'ON_HOLD'}
				if(this.filterStatus == 'SENT'){ this.filterStatus = 'SENT'}
				if(this.filterStatus == 'REQUEST_SENT'){ this.filterStatus = 'REQUEST_SENT'}
				if(this.filterStatus == 'IN_PREPARATION'){ this.filterStatus = 'IN_PREPARATION'}
		        return (c.status.toLowerCase().indexOf(this.filterStatus.toLowerCase()) >= 0)})
			.filter(c => {
		        if(this.filterDate1 == '' || this.filterDate2 == '') return true;
				this.buttonVisibility = true;
				console.log('formatiran datum ' + this.formatDate(this.filterDate1) + "("+ Date.parse(this.formatDate(this.filterDate1))+ ")")
				var date1 = this.formatDate(this.filterDate1);
				var date2 = this.formatDate(this.filterDate2);
				var dd = c.dateTimeOrder.substring(0, 2)
				var mm = c.dateTimeOrder.substring(3, 5)
				var yyyy =  c.dateTimeOrder.substring(6, 10)
				var date = mm + '-' + dd + '-'+ yyyy;
				var datum = new Date(date)
				var datumPor = this.formatDate(datum)
		       	console.log("datum 1: " + Date.parse(date1) + ", datum 2: " + Date.parse(date2))
				if(Date.parse(date1) <= Date.parse(datumPor) && Date.parse(datumPor) <= Date.parse(date2) ) return true;
				})
		},
		sortedData() {
	      if(!this.sort.field && !this.filter && !this.filterPrice1  && !this.filterPrice2 && !this.filterDate1 && !this.filterDate2 
				&& this.filterType == 'Tip restorana' && this.filterStatus == 'Status porudzbine'){
	        return this.orders
	      }
	      return this.filteredOrders.sort((a,b)=>{
		        if(this.sort.desc){
		          return (a[this.sort.field] > b[this.sort.field]) ? -1:1        
		        }else{
		          return (a[this.sort.field] > b[this.sort.field]) ? 1:-1                  
		        }
	     })
    	}
	},
	components: {
      	vuejsDatepicker
    },
	
})