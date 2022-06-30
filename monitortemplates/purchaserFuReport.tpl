<div class="card">
        <div class="card-header card-header-warning">
          <h4 class="card-title" onClick="javascript:toggleTable('purchaser')">Purchaser Report</h4>
        </div>
        <div class="card-body table-responsive">
          <table id='purchaserFuReport' class="table cell-border">
            <thead >
            <tr  >
				<th>Financial Year</th>
				<th>Constitution</th>
				<th>Status</th>
				<th>Invoice Count</th>
				<th>FactoringUnit Count</th>
				<th>Amount</th>
			</tr>
            </thead>
            <tbody>
			{{#each purchaserFuReport}}
	 			 <tr >
				    <td>{{FinancialYear}}</td>
				    <td>{{Constitution}}</td>
				    <td>{{Status}}</td>
			      	<td align="right">{{InvoiceCount}}</td>
                    <td align="right">{{FactoringUnitCount}}</td>
                	<td align="right">{{Amount}}</td>
			    </tr>
	 		{{/each}}
            </tbody>
          </table>
        </div>
        <div class="card-footer">
		  <div class="stats">
		    <i class="material-icons"></i> updated at {{currdate}} {{currtime}}
		  </div>
		</div>
 </div>
