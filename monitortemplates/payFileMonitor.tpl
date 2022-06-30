<div class="card">
        <div class="card-header card-header-warning">
          <h4 class="card-title" onClick="javascript:toggleTable('payFile')" >Pay File Details</h4>
        </div>
        <div class="card-body table-responsive">
          <table id='payFileMonitor' class="table cell-border">
            <thead>
            	<tr>
			    <th>FACILITATOR</th>
			    <th>STATUS</th>
		      	<th>FILEID</th>
		        <th>FILEDATE</th>
		     	<th>TOTALRECORDS</th>
			    <th>TOTALVALUE</th>
			    </tr>
            </thead>
            <tbody>
			{{#each payFileMonitor}}
				  <tr style="cursor:pointer;" {{#ifCond ID '==' "U"}}class='red'{{/ifCond}} {{#ifCond ID '==' "I"}}class='red'{{/ifCond}} onClick="javascript:payFile('{{FILEDATE}}')" >
				    <td>{{FACILITATOR}}</td>
				    <td>{{STATUS}}</td>
			      	<td align="right">{{FILEID}}</td>
			        <td>{{FILEDATE}}</td>
			     	<td align="right">{{TOTALRECORDS}}</td>
				    <td align="right">{{TOTALVALUE}}</td>
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
