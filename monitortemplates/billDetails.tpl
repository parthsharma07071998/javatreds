<div class="card">
        <div class="card-header card-header-warning">
          <h4 class="card-title">Bill Details</h4>
        </div>
        <div class="card-body table-responsive">
          <table class="table table-hover">
            <thead class="text-warning">
				<th> Bill Count </th>
				<th> Total Charges </th>
				<th> Total Amount </th>
				<th> Creation Month </th>
            </thead>
            <tbody>
				<tr>
					<td>{{billDetails.billcount}}</td>
					<td>{{billDetails.totalcharges}}</td>
					<td>{{billDetails.totalamount}}</td>
					<td>{{billDetails.creationmonth}}</td>
				</tr>
            </tbody>
          </table>
        </div>
        <div class="card-footer">
		  <div class="stats">
		    <i class="material-icons"></i> updated at {{currdate}} {{currtime}}
		  </div>
		</div>
 </div>